package com.example.spring_ecom.controller.api.coupon.model;

import java.math.BigDecimal;

public record CouponValidationResponse(
    boolean valid,
    String message,
    CouponResponse coupon,
    BigDecimal discountAmount
) {
    public static CouponValidationResponse valid(CouponResponse coupon, BigDecimal discountAmount) {
        return new CouponValidationResponse(true, "Coupon is valid", coupon, discountAmount);
    }
    
    public static CouponValidationResponse invalid(String message) {
        return new CouponValidationResponse(false, message, null, BigDecimal.ZERO);
    }
}
