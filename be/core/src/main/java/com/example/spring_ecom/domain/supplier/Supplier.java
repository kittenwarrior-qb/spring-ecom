package com.example.spring_ecom.domain.supplier;

import java.time.LocalDateTime;

public record Supplier(
    Long id,
    String name,
    String contactName,
    String phone,
    String email,
    String address,
    String note,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt
) {
}

