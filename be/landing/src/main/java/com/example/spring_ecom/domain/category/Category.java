package com.example.spring_ecom.domain.category;

import java.time.LocalDateTime;

public record Category(
    Long id,
    String name,
    String slug,
    String description,
    Long parentId,
    Integer displayOrder,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt
) {
}
