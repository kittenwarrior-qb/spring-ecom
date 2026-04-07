package com.example.spring_ecom.controller.api.order.orderItem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public record OrderItemResponse(
    Long id,
    Long orderId,
    Long productId,
    String productTitle,
    String productImage,
    Integer quantity,
    Integer cancelledQuantity,
    BigDecimal price,
    BigDecimal subtotal,
    String status,
    LocalDateTime createdAt,
    LocalDateTime cancelledAt
) {
    public Integer getAvailableQuantity() {
        int cancelled = Objects.nonNull(cancelledQuantity) ? cancelledQuantity : 0;
        return quantity - cancelled;
    }
}
