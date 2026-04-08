package com.example.spring_ecom.domain.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseOrder(
    Long id,
    String poNumber,
    Long supplierId,
    PurchaseOrderStatus status,
    BigDecimal totalAmount,
    String note,
    Long createdBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt
) {
}

