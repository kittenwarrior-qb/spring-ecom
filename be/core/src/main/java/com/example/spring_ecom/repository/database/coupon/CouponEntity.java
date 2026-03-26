package com.example.spring_ecom.repository.database.coupon;

import com.example.spring_ecom.repository.database.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponEntity extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "discount_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "min_order_value", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal minOrderValue = BigDecimal.ZERO;
    
    @Column(name = "max_discount", precision = 10, scale = 2)
    private BigDecimal maxDiscount;
    
    @Column(name = "usage_limit")
    private Integer usageLimit;
    
    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private Integer usedCount = 0;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
