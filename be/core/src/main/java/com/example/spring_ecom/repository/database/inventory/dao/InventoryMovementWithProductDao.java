package com.example.spring_ecom.repository.database.inventory.dao;

import com.example.spring_ecom.domain.inventory.MovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryMovementWithProductDao(
    Long id,
    Long productId,
    String productName,
    String productImage,
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
