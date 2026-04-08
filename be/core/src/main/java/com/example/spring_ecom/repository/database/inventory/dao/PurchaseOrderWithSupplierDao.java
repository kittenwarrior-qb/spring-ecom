package com.example.spring_ecom.repository.database.inventory.dao;

import com.example.spring_ecom.domain.inventory.PurchaseOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseOrderWithSupplierDao(
    Long id,
    String poNumber,
    Long supplierId,
    String supplierName,
    PurchaseOrderStatus status,
    BigDecimal totalAmount,
    String note,
    Long createdBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

