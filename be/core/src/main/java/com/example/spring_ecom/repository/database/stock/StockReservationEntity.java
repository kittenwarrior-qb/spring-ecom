package com.example.spring_ecom.repository.database.stock;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Entity tracking stock reservation per order item
 * Used for TTL-based stock release
 */
@Entity
@Table(name = "stock_reservations", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_expire_at", columnList = "expire_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.ACTIVE;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "expire_at", nullable = false)
    private Instant expireAt;
    
    @Column(name = "released_at")
    private Instant releasedAt;
    
    public enum ReservationStatus {
        ACTIVE,      // Đang giữ stock reservationQty = n
        CONFIRMED,   // Đã thanh toán, reservationQty = 0
        RELEASED,    // Đã trả về stock
        CANCELLED    // Đã hủy bởi user
    }
    
    public boolean isExpired() {
        return Instant.now().isAfter(expireAt);
    }
    
    public boolean isActive() {
        return status == ReservationStatus.ACTIVE && !isExpired();
    }
}
