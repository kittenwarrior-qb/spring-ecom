package com.example.spring_ecom.controller.api.category.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    String name,
    
    @NotBlank(message = "Slug cannot be blank")
    @Size(min = 1, max = 100, message = "Slug must be between 1 and 100 characters")
    String slug,
    
    String description,
    
    Long parentId,
    
    Integer displayOrder,
    
    Boolean isActive
) {
}
