package com.example.spring_ecom.controller.api.coupon.model;

import com.example.spring_ecom.repository.database.coupon.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
    Long id,
    String code,
    String description,
    DiscountType discountType,
    BigDecimal discountValue,
    BigDecimal minOrderValue,
    BigDecimal maxDiscount,
    Integer usageLimit,
    Integer usedCount,
    Integer remainingUses,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isActive,
    Boolean isValid,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public Boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive 
            && (startDate == null || !now.isBefore(startDate))
            && (endDate == null || !now.isAfter(endDate))
            && (usageLimit == null || usedCount < usageLimit);
    }
    
    public Integer remainingUses() {
        if (usageLimit == null) {
            return null; // Unlimited
        }
        return Math.max(0, usageLimit - usedCount);
    }
}
