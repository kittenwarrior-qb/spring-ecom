package com.example.spring_ecom.controller.api.product.model;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    String title,
    
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    String slug,
    
    String author,
    
    String publisher,
    
    @Min(value = 1000, message = "Publication year must be at least 1000")
    @Max(value = 9999, message = "Publication year must not exceed 9999")
    Integer publicationYear,
    
    String language,
    
    @Min(value = 1, message = "Pages must be at least 1")
    Integer pages,
    
    String format,
    
    String description,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    BigDecimal price,
    
    @DecimalMin(value = "0.0", message = "Discount price must be at least 0")
    BigDecimal discountPrice,
    
    @Min(value = 0, message = "Stock quantity must be at least 0")
    Integer stockQuantity,
    
    String coverImageUrl,
    
    Boolean isBestseller,
    
    Boolean isActive,
    
    Long categoryId
) {
}
