package com.example.spring_ecom.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Order(
    Long id,
    String orderNumber,
    Long userId,
    OrderStatus status,
    PaymentStatus paymentStatus,
    BigDecimal subtotal,
    BigDecimal shippingFee,
    BigDecimal discount,
    BigDecimal total,
    PaymentMethod paymentMethod,
    String shippingAddress,
    String shippingCity,
    String shippingDistrict,
    String shippingWard,
    String recipientName,
    String recipientPhone,
    String note,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime cancelledAt
) {
}
