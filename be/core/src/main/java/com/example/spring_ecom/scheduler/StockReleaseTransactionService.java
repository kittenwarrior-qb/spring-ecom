package com.example.spring_ecom.scheduler;

import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.repository.database.order.OrderRepository;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import com.example.spring_ecom.repository.database.stock.StockReservationEntity;
import com.example.spring_ecom.repository.database.stock.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockReleaseTransactionService {

    private final StockReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public void releaseOneReservation(StockReservationEntity reservation) {
        Long orderId = reservation.getOrderId();

        ProductEntity product = productRepository.findById(reservation.getProductId()).orElse(null);

        if (Objects.nonNull(product)) {
            if (product.getReservedQuantity() >= reservation.getQuantity()) {
                product.setReservedQuantity(product.getReservedQuantity() - reservation.getQuantity());
                productRepository.save(product);
                log.info("[SCHEDULER] Released expired reservation: productId={}, quantity={}, orderId={}",
                        reservation.getProductId(), reservation.getQuantity(), orderId);
            } else {
                log.warn("[SCHEDULER] Insufficient reserved quantity: productId={}, reserved={}, requested={}",
                        reservation.getProductId(), product.getReservedQuantity(), reservation.getQuantity());
            }
        } else {
            log.warn("[SCHEDULER] Product not found for release: productId={}", reservation.getProductId());
        }

        reservation.setStatus(StockReservationEntity.ReservationStatus.RELEASED);
        reservation.setReleasedAt(Instant.now());
        reservationRepository.save(reservation);

        updateOrderStatusToCancelled(orderId);
        log.info("[SCHEDULER] Reservation released and order cancelled: orderId={}", orderId);
    }

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
}

