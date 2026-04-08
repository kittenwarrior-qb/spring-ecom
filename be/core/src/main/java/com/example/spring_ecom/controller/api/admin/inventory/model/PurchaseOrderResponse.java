package com.example.spring_ecom.controller.api.admin.inventory.model;

import com.example.spring_ecom.domain.inventory.PurchaseOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderResponse(
    Long id,
    String poNumber,
    Long supplierId,
    String supplierName,
    PurchaseOrderStatus status,
    BigDecimal totalAmount,
    String note,
    Long createdBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<PurchaseOrderItemResponse> items
) {
}

