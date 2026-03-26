package com.example.spring_ecom.controller.api.order.model;

import com.example.spring_ecom.controller.api.order.orderItem.model.OrderItemResponse;
import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.domain.order.PaymentMethod;
import com.example.spring_ecom.domain.order.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
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
    BigDecimal refundedAmount,
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
