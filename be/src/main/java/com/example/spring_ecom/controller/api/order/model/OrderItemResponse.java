package com.example.spring_ecom.controller.api.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemResponse(
    Long id,
    Long orderId,
    Long productId,
    String productTitle,
    String productImage,
    Integer quantity,
    BigDecimal price,
    BigDecimal subtotal,
    LocalDateTime createdAt
) {
}
