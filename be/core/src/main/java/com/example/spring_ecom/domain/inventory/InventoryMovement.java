package com.example.spring_ecom.domain.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryMovement(
    Long id,
    Long productId,
    MovementType movementType,
    Integer quantity,
    BigDecimal costPrice,
    Integer stockBefore,
    Integer stockAfter,
    String referenceType,
    Long referenceId,
    String note,
    Long createdBy,
    LocalDateTime createdAt
) {
}

