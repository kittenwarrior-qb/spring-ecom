package com.example.spring_ecom.repository.database.order;

import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentMethod;
import com.example.spring_ecom.domain.order.PaymentStatus;
import com.example.spring_ecom.repository.database.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING_STOCK;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "shipping_fee", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;
    
    @Column(name = "discount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "refunded_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal refundedAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;
    
    @Column(name = "shipping_city", nullable = false)
    private String shippingCity;
    
    @Column(name = "shipping_district", nullable = false)
    private String shippingDistrict;
    
    @Column(name = "shipping_ward", nullable = false)
    private String shippingWard;
    
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;
    
    @Column(name = "recipient_phone", nullable = false)
    private String recipientPhone;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    @Column(name = "coupon_id")
    private Long couponId;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
