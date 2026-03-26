package com.example.spring_ecom.repository.kafka.service;

import com.example.spring_ecom.kafka.domain.OrderEvent;
import com.example.spring_ecom.repository.kafka.producer.OrderKafkaProducerImpl;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.repository.database.stock.StockReservationEntity;
import com.example.spring_ecom.repository.database.stock.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final OrderKafkaProducerImpl kafkaProducer;
    private final OrderRepository orderRepository;
    
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
            sendOrderFailedEvent(event, "Order has no items", new ArrayList<>());
            return;
        }

        // BƯỚC 1: Check ALL items trước
        List<OrderEvent.StockFailureItem> failedItems = checkStockAvailability(event);
        
        // BƯỚC 2: Nếu có fail → ROLLBACK + send ORDER_FAILED
        if (!failedItems.isEmpty()) {
            log.warn("[KAFKA] Stock check failed for OrderId: {}, Failed items: {}", 
                    event.getOrderId(), failedItems.size());
            sendOrderFailedEvent(event, "Insufficient stock", failedItems);
            return; // Transaction rollback tự động nếu throw exception
        }
        
        // BƯỚC 3: ALL OK → Reserve ALL trong 1 transaction
        try {
            reserveStockForOrder(event);
            log.info("[KAFKA] Stock reserved successfully - OrderId: {}, Items: {}", 
                    event.getOrderId(), event.getItems().size());
            
            // BƯỚC 4: Update order status directly in DB (ONE-WAY KAFKA)
            updateOrderStatus(event.getOrderId(), OrderStatus.STOCK_RESERVED);
            log.info("Order status updated to STOCK_RESERVED: orderId={}", event.getOrderId());
        } catch (Exception e) {
            log.error("[KAFKA] Failed to reserve stock for OrderId: {}", event.getOrderId(), e);
            // Update order status to STOCK_FAILED
            updateOrderStatus(event.getOrderId(), OrderStatus.STOCK_FAILED);
            log.error("Order status updated to STOCK_FAILED: orderId={}", event.getOrderId());
            throw e; // Trigger transaction rollback
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
            
            if (product == null) {
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
     * Reserve stock for ALL items in order
     * Creates reservation records and updates product's reservedQuantity
     */
    private void reserveStockForOrder(OrderEvent event) {
        Instant now = Instant.now();
        Instant expireAt = now.plus(reservationTtlMinutes, ChronoUnit.MINUTES);

        for (OrderEvent.OrderItemPayload item : event.getItems()) {
            ProductEntity product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            // Update reservedQuantity on product
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

            log.debug("Reserved stock: productId={}, quantity={}, expireAt={}",
                    item.getProductId(), item.getQuantity(), expireAt);
        }
    }
    
    /**
     * Send ORDER_CONFIRMED event back to client
     */
    private void sendOrderConfirmedEvent(OrderEvent originalEvent) {
        OrderEvent confirmedEvent = OrderEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .eventType(OrderEvent.ORDER_CONFIRMED)
                .timestamp(Instant.now())
                .source("server")
                .orderId(originalEvent.getOrderId())
                .orderNumber(originalEvent.getOrderNumber())
                .userId(originalEvent.getUserId())
                .status("STOCK_RESERVED")
                .items(originalEvent.getItems())
                .build();
        
        kafkaProducer.send(confirmedEvent);
        log.info("[KAFKA] Sent ORDER_CONFIRMED event - OrderId: {}", originalEvent.getOrderId());
    }
    
    /**
     * Send ORDER_FAILED event back to client
     */
    private void sendOrderFailedEvent(OrderEvent originalEvent, String reason, List<OrderEvent.StockFailureItem> failedItems) {
        OrderEvent failedEvent = OrderEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .eventType(OrderEvent.ORDER_FAILED)
                .timestamp(Instant.now())
                .source("server")
                .orderId(originalEvent.getOrderId())
                .orderNumber(originalEvent.getOrderNumber())
                .userId(originalEvent.getUserId())
                .status("STOCK_FAILED")
                .failureReason(reason)
                .failedItems(failedItems)
                .items(originalEvent.getItems())
                .build();
        
        kafkaProducer.send(failedEvent);
        log.info("[KAFKA] Sent ORDER_FAILED event - OrderId: {}, Reason: {}", 
                originalEvent.getOrderId(), reason);
    }

    /**
     * Release reserved stock (for cancel/timeout)
     */
    private void releaseReservedStock(Long productId, Integer quantity) {
        productRepository.findById(productId).ifPresentOrElse(
                product -> {
                    product.setReservedQuantity(Math.max(0, product.getReservedQuantity() - quantity));
                    productRepository.save(product);
                    log.info("Released reserved stock: productId={}, quantity={}, newReserved={}",
                            productId, quantity, product.getReservedQuantity());
                },
                () -> log.error("Product not found for stock release: productId={}", productId)
        );
    }
    
    /**
     * Deduct stock (convert reserved → sold) when payment confirmed
     */
    private void deductStock(Long productId, Integer quantity, String productTitle) {
        productRepository.findById(productId).ifPresentOrElse(
                product -> {
                    // Deduct from stockQuantity
                    product.setStockQuantity(product.getStockQuantity() - quantity);
                    // Release from reservedQuantity
                    product.setReservedQuantity(Math.max(0, product.getReservedQuantity() - quantity));
                    productRepository.save(product);
                    log.info("Stock deducted: productId={}, title={}, quantity={}, newStock={}, newReserved={}",
                            productId, productTitle, quantity, product.getStockQuantity(), product.getReservedQuantity());
                },
                () -> log.error("Product not found for stock deduction: productId={}", productId)
        );
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
}
