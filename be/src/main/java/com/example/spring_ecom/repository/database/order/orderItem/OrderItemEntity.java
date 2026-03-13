package com.example.spring_ecom.repository.database.order.orderItem;

import com.example.spring_ecom.domain.order.OrderItemStatus;
import com.example.spring_ecom.repository.database.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "product_title", nullable = false)
    private String productTitle;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "cancelled_quantity", nullable = false)
    @Builder.Default
    private Integer cancelledQuantity = 0;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OrderItemStatus status = OrderItemStatus.ACTIVE;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
