package com.example.spring_ecom.controller.api.review.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request body for updating a product review")
public record UpdateReviewRequest(
        @Schema(description = "Rating (1-5 stars)", example = "4", required = true)
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        Integer rating,
        
        @Schema(description = "Review title", example = "Good book")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,
        
        @Schema(description = "Review comment", example = "Updated review content")
        @Size(max = 2000, message = "Comment must not exceed 2000 characters")
        String comment
) {
}
