package com.example.spring_ecom.repository.dao.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemWithProductDao(
    Long id,
    Long orderId,
    Long productId,
    String productTitle,
    String productCoverImageUrl,
    Integer quantity,
    BigDecimal price,
    BigDecimal subtotal,
    LocalDateTime createdAt
) {
}