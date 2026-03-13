package com.example.spring_ecom.repository.database.order.dao;

import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderWithUserDao(
    Long id,
    String orderNumber,
    Long userId,
    String userEmail,
    OrderStatus status,
    PaymentStatus paymentStatus,
    BigDecimal subtotal,
    BigDecimal shippingFee,
    BigDecimal discount,
    BigDecimal total,
    String paymentMethod,
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