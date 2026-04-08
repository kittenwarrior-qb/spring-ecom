package com.example.spring_ecom.controller.api.admin.inventory.model;

import java.math.BigDecimal;

public record PurchaseOrderItemResponse(
    Long id,
    Long productId,
    String productTitle,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice
) {
}

