package com.example.spring_ecom.controller.api.admin.supplier.model;

import java.time.LocalDateTime;

public record SupplierResponse(
    Long id,
    String name,
    String contactName,
    String phone,
    String email,
    String address,
    String note,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

