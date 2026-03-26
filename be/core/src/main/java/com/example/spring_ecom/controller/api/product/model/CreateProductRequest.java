package com.example.spring_ecom.controller.api.product.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating new product (book)
 * SERVER SERVICE - Admin only
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;
    
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;
    
    @Size(max = 255, message = "Publisher must not exceed 255 characters")
    private String publisher;
    
    @Min(value = 1000, message = "Publication year must be at least 1000")
    @Max(value = 2100, message = "Publication year must not exceed 2100")
    private Integer publicationYear;
    
    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language = "Vietnamese";
    
    @Min(value = 1, message = "Pages must be at least 1")
    private Integer pages;
    
    @Pattern(regexp = "Paperback|Hardcover|Ebook|Audiobook", message = "Format must be one of: Paperback, Hardcover, Ebook, Audiobook")
    private String format = "Paperback";
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal price;
    
    @DecimalMin(value = "0.0", message = "Discount price must be at least 0")
    @Digits(integer = 8, fraction = 2, message = "Discount price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal discountPrice;
    
    @Min(value = 0, message = "Stock quantity must be at least 0")
    private Integer stockQuantity = 0;
    
    @DecimalMin(value = "0.0", message = "Weight must be at least 0")
    private BigDecimal weight;
    
    @Size(max = 50, message = "Dimensions must not exceed 50 characters")
    private String dimensions;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private String coverImageUrl;
    
    private Boolean isFeatured = false;
    
    private Boolean isBestseller = false;
    
    private Boolean isActive = true;
}