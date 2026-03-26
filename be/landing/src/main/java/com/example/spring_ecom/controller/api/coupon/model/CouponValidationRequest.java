package com.example.spring_ecom.controller.api.coupon.model;

import java.math.BigDecimal;

public record CouponValidationRequest(
    String code,
    BigDecimal orderTotal
) {}
