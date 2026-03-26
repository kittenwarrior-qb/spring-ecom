package com.example.spring_ecom.repository.database.order.dao;

import com.example.spring_ecom.domain.order.PaymentMethod;

import java.math.BigDecimal;

public record CreateOrderEntityDao(
    Long userId,
    PaymentMethod paymentMethod,
    String shippingAddress,
    String shippingCity,
    String shippingDistrict,
    String shippingWard,
    String recipientName,
    String recipientPhone,
    String note,
    BigDecimal subtotal,
    BigDecimal shippingFee,
    BigDecimal discount,
    BigDecimal total,
    Long couponId
) {
}