package com.example.spring_ecom.controller.api.order.model;

import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
    Long id,
    String orderNumber,
    Long userId,
    OrderStatus status,
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
    List<OrderItemResponse> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime cancelledAt
) {
}
