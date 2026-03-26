package com.example.spring_ecom.controller.api.category.model;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
    @NotBlank(message = "Category name cannot be blank")
    String name,
    
    String slug,
    
    String description,
    
    Long parentId,
    
    Integer displayOrder,
    
    Boolean isActive
) {
}
