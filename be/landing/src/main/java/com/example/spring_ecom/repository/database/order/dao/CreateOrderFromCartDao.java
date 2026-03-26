package com.example.spring_ecom.repository.database.order.dao;

import com.example.spring_ecom.domain.order.PaymentMethod;

public record CreateOrderFromCartDao(
    Long userId,
    PaymentMethod paymentMethod,
    String shippingAddress,
    String shippingCity,
    String shippingDistrict,
    String shippingWard,
    String recipientName,
    String recipientPhone,
    String note,
    String couponCode
) {
}