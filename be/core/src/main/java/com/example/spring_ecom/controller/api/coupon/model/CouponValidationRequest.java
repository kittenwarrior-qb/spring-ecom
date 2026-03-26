package com.example.spring_ecom.controller.api.coupon.model;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CouponValidationRequest(
    @NotBlank(message = "Coupon code is required")
    String code,
    
    @NotNull(message = "Order total is required")
    @Positive(message = "Order total must be positive")
    BigDecimal orderTotal
) {}
