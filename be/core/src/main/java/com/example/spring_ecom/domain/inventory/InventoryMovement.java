package com.example.spring_ecom.domain.inventory;

import java.time.LocalDateTime;

public record InventoryMovement(
    Long id,
    Long productId,
    MovementType movementType,
    Integer quantity,
    String referenceType,
    Long referenceId,
    String note,
    Long createdBy,
    LocalDateTime createdAt
) {
}

