package com.example.spring_ecom.controller.api.coupon.model;

import com.example.spring_ecom.repository.database.coupon.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

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
            && (Objects.isNull(startDate) || !now.isBefore(startDate))
            && (Objects.isNull(endDate) || !now.isAfter(endDate))
            && (Objects.isNull(usageLimit) || usedCount < usageLimit);
    }
    
    public Integer remainingUses() {
        if (Objects.isNull(usageLimit)) {
            return null; // Unlimited
        }
        return Math.max(0, usageLimit - usedCount);
    }
}
