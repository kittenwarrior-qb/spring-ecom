package com.example.spring_ecom.controller.api.coupon.model;

import com.example.spring_ecom.repository.database.coupon.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record CouponRequest(
    @NotBlank(message = "Coupon code is required")
    @Size(max = 50, message = "Coupon code cannot exceed 50 characters")
    String code,
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    String description,
    
    @NotNull(message = "Discount type is required")
    DiscountType discountType,
    
    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    BigDecimal discountValue,
    
    @PositiveOrZero(message = "Minimum order value cannot be negative")
    BigDecimal minOrderValue,
    
    @Positive(message = "Max discount must be positive")
    BigDecimal maxDiscount,
    
    @Positive(message = "Usage limit must be positive")
    Integer usageLimit,
    
    @NotNull(message = "Start date is required")
    LocalDateTime startDate,
    
    @NotNull(message = "End date is required")
    LocalDateTime endDate,
    
    Boolean isActive,
    
    NotificationType notificationType,
    
    List<Long> targetUserIds
) {
    public CouponRequest {
        // Set defaults
        if (Objects.isNull(minOrderValue)) {
            minOrderValue = BigDecimal.ZERO;
        }
        if (Objects.isNull(isActive)) {
            isActive = true;
        }
        if (Objects.isNull(notificationType)) {
            notificationType = NotificationType.NONE;
        }
    }
    
    /**
     * Notification type for coupon creation
     */
    public enum NotificationType {
        NONE,       // Don't send notification
        BROADCAST,  // Send to all users
        TARGETED    // Send to specific users
    }
}
