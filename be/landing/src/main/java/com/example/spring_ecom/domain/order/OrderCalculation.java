package com.example.spring_ecom.domain.order;

import java.math.BigDecimal;

public record OrderCalculation(
    BigDecimal subtotal,
    BigDecimal shippingFee,
    BigDecimal discount,
    BigDecimal total,
    Long couponId
) {
    public OrderCalculation {
        if (couponId == null) {
            couponId = null;
        }
    }
    
    public static OrderCalculation withoutCoupon(BigDecimal subtotal, BigDecimal shippingFee) {
        return new OrderCalculation(subtotal, shippingFee, BigDecimal.ZERO, subtotal.add(shippingFee), null);
    }
    
    public static OrderCalculation withCoupon(BigDecimal subtotal, BigDecimal shippingFee, BigDecimal discount, Long couponId) {
        return new OrderCalculation(subtotal, shippingFee, discount, subtotal.add(shippingFee).subtract(discount), couponId);
    }
}