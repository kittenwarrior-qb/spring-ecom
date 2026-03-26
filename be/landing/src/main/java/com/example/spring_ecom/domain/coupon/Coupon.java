package com.example.spring_ecom.domain.coupon;

import com.example.spring_ecom.repository.database.coupon.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Coupon(
    Long id,
    String code,
    String description,
    DiscountType discountType,
    BigDecimal discountValue,
    BigDecimal minOrderValue,
    BigDecimal maxDiscount,
    Integer usageLimit,
    Integer usedCount,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt
) {
    /**
     * Calculate discount amount for a given order total
     */
    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        if (orderTotal.compareTo(minOrderValue) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount;
        if (discountType == DiscountType.PERCENTAGE) {
            discount = orderTotal.multiply(discountValue).divide(BigDecimal.valueOf(100));
        } else {
            discount = discountValue;
        }
        
        // Apply max discount cap if set
        if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
            discount = maxDiscount;
        }
        
        // Discount cannot exceed order total
        if (discount.compareTo(orderTotal) > 0) {
            discount = orderTotal;
        }
        
        return discount;
    }
    
    /**
     * Check if coupon is currently valid
     */
    public boolean isValidNow() {
        LocalDateTime now = LocalDateTime.now();
        return isActive 
            && deletedAt == null
            && (startDate == null || !now.isBefore(startDate))
            && (endDate == null || !now.isAfter(endDate))
            && (usageLimit == null || usedCount < usageLimit);
    }
    
    /**
     * Check if coupon can be applied to the given order total
     */
    public boolean canApplyTo(BigDecimal orderTotal) {
        return isValidNow() && orderTotal.compareTo(minOrderValue) >= 0;
    }
}
