package com.example.spring_ecom.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItem(
    Long id,
    Long orderId,
    Long productId,
    String productTitle,
    Integer quantity,
    BigDecimal price,
    BigDecimal subtotal,
    LocalDateTime createdAt
) {
}
