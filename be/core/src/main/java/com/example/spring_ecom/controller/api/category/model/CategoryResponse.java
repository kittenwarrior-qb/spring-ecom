package com.example.spring_ecom.controller.api.category.model;

import java.time.LocalDateTime;

public record CategoryResponse(
    Long id,
    String name,
    String slug,
    String description,
    Long parentId,
    String parentName,
    Integer displayOrder,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
