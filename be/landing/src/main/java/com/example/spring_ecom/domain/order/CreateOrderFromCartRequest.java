package com.example.spring_ecom.domain.order;

public record CreateOrderFromCartRequest(
    Long userId,
    PaymentMethod paymentMethod,
    String shippingAddress,
    String shippingCity,
    String shippingDistrict,
    String shippingWard,
    String recipientName,
    String recipientPhone,
    String note
) {
}