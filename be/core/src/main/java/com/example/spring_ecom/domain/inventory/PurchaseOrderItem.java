package com.example.spring_ecom.domain.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseOrderItem(
    Long id,
    Long purchaseOrderId,
    Long productId,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

