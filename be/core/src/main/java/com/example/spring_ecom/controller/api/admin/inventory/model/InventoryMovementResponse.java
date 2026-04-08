package com.example.spring_ecom.controller.api.admin.inventory.model;

import com.example.spring_ecom.domain.inventory.MovementType;

import java.time.LocalDateTime;

public record InventoryMovementResponse(
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

