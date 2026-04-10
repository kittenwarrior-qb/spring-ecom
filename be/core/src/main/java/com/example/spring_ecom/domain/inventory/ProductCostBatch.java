package com.example.spring_ecom.domain.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductCostBatch(
    Long id,
    Long productId,
    Long purchaseOrderItemId,
    Integer quantityRemaining,
    BigDecimal costPrice,
    LocalDateTime receivedAt,
    LocalDateTime createdAt
) {
}

