package com.example.spring_ecom.controller.api.review.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request body for creating a product review")
public record CreateReviewRequest(
        @Schema(description = "Product ID", example = "1", required = true)
        @NotNull(message = "Product ID is required")
        Long productId,
        
        @Schema(description = "Rating (1-5 stars)", example = "5", required = true)
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        Integer rating,
        
        @Schema(description = "Review title", example = "Great book!")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,
        
        @Schema(description = "Review comment", example = "This book is amazing! Highly recommended.")
        @Size(max = 2000, message = "Comment must not exceed 2000 characters")
        String comment
) {
}
