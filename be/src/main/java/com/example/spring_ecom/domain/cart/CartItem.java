package com.example.spring_ecom.domain.cart;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItem(
    Long id,
    Long cartId,
    Long productId,
    Integer quantity,
    BigDecimal price,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
