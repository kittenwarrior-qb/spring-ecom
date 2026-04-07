package com.example.spring_ecom.repository.kafka.service;

import com.example.spring_ecom.kafka.domain.OrderEvent;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.order.OrderRepository;

import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.repository.database.stock.StockReservationEntity;
import com.example.spring_ecom.repository.database.stock.StockReservationRepository;
import com.example.spring_ecom.service.notification.NotificationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service xử lý các Kafka event liên quan đến Order
 * Chịu trách nhiệm: deduct stock, restore stock, update sold count
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventService {

    private final ProductRepository productRepository;
    private final StockReservationRepository reservationRepository;
    private final OrderRepository orderRepository;
    private final NotificationUseCase notificationUseCase;

    /**
     * Idempotency guard: tracks eventIds already processed for notifications.
     * Prevents duplicate notifications when Kafka redelivers the same event.
     * NOTE: In-memory only — suitable for single-instance deployments.
     * For multi-instance scale-out, replace with a Redis SET (TTL ~24h).
     */
    private final Set<String> processedNotificationEventIds =
            Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    @Value("${stock.reservation.ttl-minutes:15}")
    private int reservationTtlMinutes;

    // ========== PUBLIC METHODS ==========

    @Transactional
    public void handleOrderCreated(OrderEvent event) {
        log.info("[KAFKA] Processing ORDER_CREATED - OrderId: {}, Items: {}", 
                event.getOrderId(), 
                Objects.nonNull(event.getItems()) ? event.getItems().size() : 0);
        
        if (hasNoItems(event)) {
            log.warn("Order {} has no items, skipping stock reservation", event.getOrderId());
            // Throw exception to trigger transaction rollback
            throw new RuntimeException("Order has no items: " + event.getOrderId());
        }

        // ATOMIC RESERVE - check + reserve in single SQL (no race condition)
        List<OrderEvent.StockFailureItem> failedItems = reserveStockForOrderAtomic(event);
        
        // If any item failed → transaction will rollback
        if (!failedItems.isEmpty()) {
            log.warn("[KAFKA] Stock reservation failed for OrderId: {}, Failed items: {}", 
                    event.getOrderId(), failedItems.size());
            // Throw exception to trigger transaction rollback
            throw new RuntimeException("Insufficient stock for order: " + event.getOrderId());
        }
        
        log.info("[KAFKA] Stock reserved successfully - OrderId: {}, Items: {}", 
                event.getOrderId(), event.getItems().size());
        
        // Update order status
        updateOrderStatus(event.getOrderId(), OrderStatus.STOCK_RESERVED);
        log.info("Order status updated to STOCK_RESERVED: orderId={}", event.getOrderId());
        
        // Send notification to user — guarded by idempotency check
        if (isNewNotificationEvent(event.getEventId())) {
            sendOrderCreatedNotification(event);
        } else {
            log.info("[NOTIFICATION] Skipping duplicate ORDER_CREATED notification - eventId={}", event.getEventId());
        }
    }

    /**
     * Handle ORDER_PAID - payment confirmed, deduct stock (reserved → sold)
     */
    @Transactional
    public void handleOrderPaid(OrderEvent event) {
        log.info("[KAFKA] Processing ORDER_PAID - OrderId: {}, Items to deduct: {}", 
                event.getOrderId(),
                Objects.nonNull(event.getItems()) ? event.getItems().size() : 0);
        
        if (hasNoItems(event)) {
            log.warn("Order {} has no items to deduct", event.getOrderId());
            return;
        }

        // Deduct stock (convert reserved → sold)
        event.getItems().forEach(item -> 
                deductStock(item.getProductId(), item.getQuantity(), item.getProductTitle()));
        
        // Update reservation status to CONFIRMED
        reservationRepository.updateStatusByOrderId(
                event.getOrderId(), 
                StockReservationEntity.ReservationStatus.CONFIRMED, 
                Instant.now());
        
        log.info("[KAFKA] ORDER_PAID processed - OrderId: {}, Stock deducted for {} products", 
                event.getOrderId(), event.getItems().size());
    }

    @Transactional
    public void handleOrderCancelled(OrderEvent event) {
        log.info("[KAFKA] Processing ORDER_CANCELLED - OrderId: {}, Items to release: {}", 
                event.getOrderId(),
                Objects.nonNull(event.getItems()) ? event.getItems().size() : 0);
        
        if (hasNoItems(event)) {
            log.warn("Order {} has no items to release", event.getOrderId());
            return;
        }

        // RELEASE reserved stock (reserved → available)
        event.getItems().forEach(item -> 
                releaseReservedStock(item.getProductId(), item.getQuantity()));
        
        // Update reservation status to CANCELLED
        reservationRepository.updateStatusByOrderId(
                event.getOrderId(), 
                StockReservationEntity.ReservationStatus.CANCELLED, 
                Instant.now());
        
        log.info("[KAFKA] ORDER_CANCELLED processed - OrderId: {}, Reserved stock released for {} products", 
                event.getOrderId(), event.getItems().size());
    }

    @Transactional
    public void handleOrderDelivered(OrderEvent event) {
        log.info("[KAFKA] Processing ORDER_DELIVERED - OrderId: {}", event.getOrderId());
        
        if (hasNoItems(event)) {
            log.warn("Order {} has no items", event.getOrderId());
            return;
        }

        Map<Long, Integer> soldCountMap = new HashMap<>();
        event.getItems().forEach(item -> 
                soldCountMap.merge(item.getProductId(), item.getQuantity(), Integer::sum));
        
        soldCountMap.forEach(this::updateSoldCount);
        
        log.info("[KAFKA] ORDER_DELIVERED processed - OrderId: {}, Sold count updated for {} products", 
                event.getOrderId(), soldCountMap.size());
    }
    
    /**
     * Handle ORDER_STATUS_CHANGED - send notification to user
     */
    @Transactional
    public void handleOrderStatusChanged(OrderEvent event) {
        log.info("[KAFKA] Processing ORDER_STATUS_CHANGED - OrderId: {}, Status: {} -> {}", 
                event.getOrderId(), event.getPreviousStatus(), event.getStatus());
        
        // Send notification — guarded by idempotency check
        if (isNewNotificationEvent(event.getEventId())) {
            sendOrderStatusNotification(event);
        } else {
            log.info("[NOTIFICATION] Skipping duplicate ORDER_STATUS_CHANGED notification - eventId={}", event.getEventId());
        }
    }
    
    /**
     * Send notification when order status changes
     */
    private void sendOrderStatusNotification(OrderEvent event) {
        try {
            if (Objects.isNull(event.getUserId()) || Objects.isNull(event.getStatus())) {
                log.warn("[NOTIFICATION] Missing userId or status, skipping notification");
                return;
            }
            
            String status = event.getStatus();
            String title = "Cập nhật đơn hàng";
            String message = mapStatusToMessage(event.getOrderNumber(), status);
            String type = mapStatusToType(status);
            String actionUrl = "/orders/" + event.getOrderId();
            
            log.info("[NOTIFICATION] Sending order status notification - userId={}, orderNumber={}, status={}", 
                    event.getUserId(), event.getOrderNumber(), status);
            
            notificationUseCase.createAndSend(
                    event.getUserId(),
                    type,
                    title,
                    message,
                    event.getOrderId(),
                    "ORDER",
                    null,
                    actionUrl
            );
            
            log.info("[NOTIFICATION] Order status notification sent successfully - userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("[NOTIFICATION] Failed to send order status notification: {}", e.getMessage(), e);
        }
    }
    
    private String mapStatusToMessage(String orderNumber, String status) {
        String statusText = switch (status.toUpperCase()) {
            case "CONFIRMED" -> "đã được xác nhận";
            case "PROCESSING" -> "đang được xử lý";
            case "SHIPPED" -> "đã được giao cho đơn vị vận chuyển";
            case "DELIVERED" -> "đã được giao thành công";
            case "CANCELLED" -> "đã bị hủy";
            case "STOCK_RESERVED" -> "đã được xác nhận tồn kho";
            default -> "đã được cập nhật";
        };
        return String.format("Đơn hàng #%s %s", orderNumber, statusText);
    }
    
    private String mapStatusToType(String status) {
        return switch (status.toUpperCase()) {
            case "CONFIRMED" -> "ORDER_CONFIRMED";
            case "PROCESSING" -> "ORDER_STATUS";
            case "SHIPPED" -> "ORDER_SHIPPED";
            case "DELIVERED" -> "ORDER_DELIVERED";
            case "CANCELLED" -> "ORDER_CANCELLED";
            case "STOCK_RESERVED" -> "ORDER_CONFIRMED";
            default -> "ORDER_STATUS";
        };
    }
    
    @Transactional
    public void handleOrderPartialCancelled(OrderEvent event) {
        log.info("[KAFKA] Processing ORDER_PARTIAL_CANCELLED - OrderId: {}, Items to release: {}",
                event.getOrderId(),
                Objects.nonNull(event.getItems()) ? event.getItems().size() : 0);
        
        if (hasNoItems(event)) {
            log.warn("Order {} has no items to release", event.getOrderId());
            return;
        }

        // RELEASE partial reserved stock
        event.getItems().forEach(item -> 
                releaseReservedStock(item.getProductId(), item.getQuantity()));
        
        // Update reservation quantities (partial)
        List<StockReservationEntity> reservations = reservationRepository.findByOrderId(event.getOrderId());
        for (StockReservationEntity reservation : reservations) {
            for (OrderEvent.OrderItemPayload item : event.getItems()) {
                if (reservation.getProductId().equals(item.getProductId())) {
                    // Reduce reserved quantity
                    reservation.setQuantity(reservation.getQuantity() - item.getQuantity());
                    reservationRepository.save(reservation);
                    break;
                }
            }
        }
        
        log.info("[KAFKA] ORDER_PARTIAL_CANCELLED processed - OrderId: {}, Partial reserved stock released for {} products",
                event.getOrderId(), event.getItems().size());
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Returns true if this eventId has NOT been processed yet (i.e. it is new).
     * Marks the eventId as processed on first call.
     * Guards against duplicate notifications from Kafka redelivery.
     */
    private boolean isNewNotificationEvent(String eventId) {
        if (Objects.isNull(eventId)) return true;
        return processedNotificationEventIds.add(eventId);
    }

    private boolean hasNoItems(OrderEvent event) {
        return Objects.isNull(event.getItems()) || event.getItems().isEmpty();
    }

    /**
     * Check stock availability for ALL items
     * @return List of failed items (empty if all OK)
     */
    private List<OrderEvent.StockFailureItem> checkStockAvailability(OrderEvent event) {
        List<OrderEvent.StockFailureItem> failedItems = new ArrayList<>();
        
        for (OrderEvent.OrderItemPayload item : event.getItems()) {
            ProductEntity product = productRepository.findById(item.getProductId()).orElse(null);
            
            if (Objects.isNull(product)) {
                failedItems.add(OrderEvent.StockFailureItem.builder()
                        .productId(item.getProductId())
                        .productTitle(item.getProductTitle())
                        .requestedQuantity(item.getQuantity())
                        .availableQuantity(0)
                        .build());
                continue;
            }
            
            int availableQuantity = product.getStockQuantity() - product.getReservedQuantity();
            if (availableQuantity < item.getQuantity()) {
                failedItems.add(OrderEvent.StockFailureItem.builder()
                        .productId(item.getProductId())
                        .productTitle(product.getTitle())
                        .requestedQuantity(item.getQuantity())
                        .availableQuantity(availableQuantity)
                        .build());
            }
        }
        
        return failedItems;
    }
    
    /**
     * Reserve stock for ALL items in order with PESSIMISTIC LOCK
     * Prevents race condition by locking product row during update
     * @return List of failed items (empty if all OK)
     */
    private List<OrderEvent.StockFailureItem> reserveStockForOrderAtomic(OrderEvent event) {
        Instant now = Instant.now();
        Instant expireAt = now.plus(reservationTtlMinutes, ChronoUnit.MINUTES);
        List<OrderEvent.StockFailureItem> failedItems = new ArrayList<>();

        for (OrderEvent.OrderItemPayload item : event.getItems()) {
            // PESSIMISTIC LOCK - lock product row for update
            ProductEntity product = productRepository.findByIdWithLock(item.getProductId())
                    .orElse(null);
            
            if (Objects.isNull(product)) {
                failedItems.add(OrderEvent.StockFailureItem.builder()
                        .productId(item.getProductId())
                        .productTitle(item.getProductTitle())
                        .requestedQuantity(item.getQuantity())
                        .availableQuantity(0)
                        .build());
                log.warn("[STOCK] Product not found: productId={}", item.getProductId());
                continue;
            }
            
            int availableQuantity = product.getStockQuantity() - product.getReservedQuantity();
            
            if (availableQuantity < item.getQuantity()) {
                failedItems.add(OrderEvent.StockFailureItem.builder()
                        .productId(item.getProductId())
                        .productTitle(product.getTitle())
                        .requestedQuantity(item.getQuantity())
                        .availableQuantity(availableQuantity)
                        .build());
                log.warn("[STOCK] Reserve failed: productId={}, requested={}, available={}", 
                        item.getProductId(), item.getQuantity(), availableQuantity);
                continue;
            }
            
            // Reserve stock - lock prevents race condition
            product.setReservedQuantity(product.getReservedQuantity() + item.getQuantity());
            productRepository.save(product);
            
            // Create reservation record
            StockReservationEntity reservation = StockReservationEntity.builder()
                    .orderId(event.getOrderId())
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .status(StockReservationEntity.ReservationStatus.ACTIVE)
                    .createdAt(now)
                    .expireAt(expireAt)
                    .build();
            reservationRepository.save(reservation);
            
            log.info("[STOCK] Reserved: productId={}, qty={}, orderId={}", 
                    item.getProductId(), item.getQuantity(), event.getOrderId());
        }
        
        return failedItems;
    }

    private void releaseReservedStock(Long productId, Integer quantity) {
        ProductEntity product = productRepository.findByIdWithLock(productId).orElse(null);
        
        if (Objects.isNull(product)) {
            log.warn("[STOCK] Release failed - product not found: productId={}", productId);
            return;
        }
        
        if (product.getReservedQuantity() < quantity) {
            log.warn("[STOCK] Release failed - insufficient reserved: productId={}, reserved={}, requested={}", 
                    productId, product.getReservedQuantity(), quantity);
            return;
        }
        
        product.setReservedQuantity(product.getReservedQuantity() - quantity);
        productRepository.save(product);
        
        log.info("[STOCK] Released: productId={}, qty={}", productId, quantity);
    }
    
    /**
     * Deduct stock (convert reserved → sold) when payment confirmed with PESSIMISTIC LOCK
     */
    private void deductStock(Long productId, Integer quantity, String productTitle) {
        ProductEntity product = productRepository.findByIdWithLock(productId).orElse(null);
        
        if (Objects.isNull(product)) {
            log.error("[STOCK] Deduct failed - product not found: productId={}", productId);
            throw new IllegalStateException("Product not found: productId=" + productId);
        }
        
        if (product.getStockQuantity() < quantity || product.getReservedQuantity() < quantity) {
            log.error("[STOCK] Deduct failed - insufficient stock or reservation: productId={}, stock={}, reserved={}, requested={}", 
                    productId, product.getStockQuantity(), product.getReservedQuantity(), quantity);
            throw new IllegalStateException("Insufficient stock or reservation: productId=" + productId);
        }
        
        product.setStockQuantity(product.getStockQuantity() - quantity);
        product.setReservedQuantity(product.getReservedQuantity() - quantity);
        product.setSoldCount(product.getSoldCount() + quantity);
        productRepository.save(product);
        
        log.info("[STOCK] Deducted: productId={}, qty={}, productTitle={}", productId, quantity, productTitle);
    }
    
    private void restoreStock(Long productId, Integer quantity, String productTitle) {
        productRepository.findById(productId).ifPresentOrElse(
                product -> {
                    product.setStockQuantity(product.getStockQuantity() + quantity);
                    productRepository.save(product);
                    log.info("Stock restored: productId={}, title={}, quantity={}, newStock={}",
                            productId, productTitle, quantity, product.getStockQuantity());
                },
                () -> log.error("Product not found for stock restore: productId={}", productId)
        );
    }

    private void updateSoldCount(Long productId, Integer quantity) {
        productRepository.findById(productId).ifPresentOrElse(
                product -> {
                    product.setSoldCount(product.getSoldCount() + quantity);
                    productRepository.save(product);
                    log.info("Sold count updated: productId={}, quantity={}, newSoldCount={}",
                            productId, quantity, product.getSoldCount());
                },
                () -> log.error("Product not found for sold count update: productId={}", productId)
        );
    }

    /**
     * Update order status directly in DB (ONE-WAY KAFKA pattern)
     * Server và Client dùng cùng DB, nên update trực tiếp thay vì gửi Kafka event
     */
    private void updateOrderStatus(Long orderId, OrderStatus status) {
        orderRepository.findById(orderId).ifPresentOrElse(
                order -> {
                    order.setStatus(status);
                    orderRepository.save(order);
                    log.info("Order status updated directly in DB: orderId={}, newStatus={}", orderId, status);
                },
                () -> log.error("Order not found for status update: orderId={}", orderId)
        );
    }
    
    /**
     * Send notification to user when order is created successfully
     */
    private void sendOrderCreatedNotification(OrderEvent event) {
        try {
            log.info("[NOTIFICATION] Sending order created notification - userId={}, orderNumber={}", 
                    event.getUserId(), event.getOrderNumber());
            
            String title = "Đặt hàng thành công";
            String message = String.format("Đơn hàng #%s đã được tạo thành công và đang chờ xác nhận", 
                    event.getOrderNumber());
            String actionUrl = "/orders/" + event.getOrderId();
            
            notificationUseCase.createAndSend(
                    event.getUserId(),
                    "ORDER_CREATED",
                    title,
                    message,
                    event.getOrderId(),
                    "ORDER",
                    null,
                    actionUrl
            );
            
            log.info("[NOTIFICATION] Order created notification sent successfully - userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("[NOTIFICATION] Failed to send order created notification: {}", e.getMessage(), e);
            // Don't fail the order if notification fails
        }
    }
}
