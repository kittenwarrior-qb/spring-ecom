package com.example.spring_ecom.scheduler;

import com.example.spring_ecom.kafka.domain.OrderEvent;
import com.example.spring_ecom.repository.kafka.producer.OrderKafkaProducerImpl;
import com.example.spring_ecom.repository.database.order.OrderEntity;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.repository.database.stock.StockReservationEntity;
import com.example.spring_ecom.repository.database.stock.StockReservationRepository;
import com.example.spring_ecom.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler để release expired stock reservations
 * Chạy mỗi phút để check reservations đã hết hạn
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockReservationScheduler {
    
    private final StockReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderKafkaProducerImpl kafkaProducer;
    
    /**
     * Run every minute to release expired reservations
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredReservations() {
        Instant now = Instant.now();
        List<StockReservationEntity> expiredReservations = reservationRepository.findExpiredReservations(now);
        
        if (expiredReservations.isEmpty()) {
            return;
        }
        
        log.info("[SCHEDULER] Found {} expired stock reservations", expiredReservations.size());
        
        for (StockReservationEntity reservation : expiredReservations) {
            try {
                releaseReservation(reservation);
            } catch (Exception e) {
                log.error("Failed to release reservation: id={}, orderId={}", 
                        reservation.getId(), reservation.getOrderId(), e);
            }
        }
    }
    
    /**
     * Release single reservation and notify client
     */
    private void releaseReservation(StockReservationEntity reservation) {
        Long orderId = reservation.getOrderId();
        
        // Update product's reservedQuantity
        ProductEntity product = productRepository.findById(reservation.getProductId()).orElse(null);
        if (product != null) {
            product.setReservedQuantity(Math.max(0, product.getReservedQuantity() - reservation.getQuantity()));
            productRepository.save(product);
            log.info("Released expired reservation: productId={}, quantity={}, orderId={}", 
                    reservation.getProductId(), reservation.getQuantity(), orderId);
        }
        
        // Update reservation status
        reservation.setStatus(StockReservationEntity.ReservationStatus.RELEASED);
        reservation.setReleasedAt(Instant.now());
        reservationRepository.save(reservation);
        
        // Update order status to CANCELLED (reservation expired = payment timeout)
        updateOrderStatusToCancelled(orderId);
        
        // Send STOCK_RELEASED event to client
        sendStockReleasedEvent(reservation);
    }
    
    /**
     * Update order status to CANCELLED when reservation expires
     */
    private void updateOrderStatusToCancelled(Long orderId) {
        orderRepository.findById(orderId).ifPresentOrElse(
                order -> {
                    if (order.getStatus() == OrderStatus.STOCK_RESERVED) {
                        order.setStatus(OrderStatus.CANCELLED);
                        order.setCancelledAt(LocalDateTime.now());
                        orderRepository.save(order);
                        log.info("Order status updated to CANCELLED due to reservation expiry: orderId={}", orderId);
                    } else {
                        log.debug("Order not in STOCK_RESERVED status, skipping cancellation: orderId={}, status={}", 
                                orderId, order.getStatus());
                    }
                },
                () -> log.warn("Order not found for status update: orderId={}", orderId)
        );
    }
    
    /**
     * Send STOCK_RELEASED event to client
     */
    private void sendStockReleasedEvent(StockReservationEntity reservation) {
        OrderEvent event = OrderEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .eventType(OrderEvent.STOCK_RELEASED)
                .timestamp(Instant.now())
                .source("server")
                .orderId(reservation.getOrderId())
                .status("STOCK_RELEASED")
                .failureReason("Reservation expired (TTL)")
                .build();
        
        kafkaProducer.send(event);
        log.info("[KAFKA] Sent STOCK_RELEASED event for expired reservation: orderId={}", reservation.getOrderId());
    }
}
