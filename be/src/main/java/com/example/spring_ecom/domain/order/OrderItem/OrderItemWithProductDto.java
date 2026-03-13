package com.example.spring_ecom.domain.order.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemWithProductDto(
    Long id,
    Long orderId,
    Long productId,
    String productTitle,
    String productCoverImageUrl,
    Integer quantity,
    Integer cancelledQuantity,
    BigDecimal price,
    BigDecimal subtotal,
    String status,
    LocalDateTime createdAt,
    LocalDateTime cancelledAt
) {
}