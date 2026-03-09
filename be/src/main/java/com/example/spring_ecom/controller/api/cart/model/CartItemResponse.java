package com.example.spring_ecom.controller.api.cart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponse(
    Long id,
    Long cartId,
    Long productId,
    Integer quantity,
    BigDecimal price,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
