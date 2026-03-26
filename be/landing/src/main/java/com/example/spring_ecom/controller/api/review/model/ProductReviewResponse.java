package com.example.spring_ecom.controller.api.review.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Product review response with user and admin info")
public record ProductReviewResponse(
        @Schema(description = "Review ID", example = "1")
        Long id,
        
        @Schema(description = "Product ID", example = "1")
        Long productId,
        
        @Schema(description = "User ID", example = "1")
        Long userId,
        
        @Schema(description = "Username", example = "john_doe")
        String username,
        
        @Schema(description = "Rating (1-5 stars)", example = "5")
        Integer rating,
        
        @Schema(description = "Review title", example = "Great book!")
        String title,
        
        @Schema(description = "Review comment")
        String comment,
        
        @Schema(description = "Is verified purchase", example = "true")
        Boolean isVerifiedPurchase,
        
        @Schema(description = "Like count", example = "10")
        Integer likeCount,
        
        @Schema(description = "Dislike count", example = "2")
        Integer dislikeCount,
        
        @Schema(description = "Admin reply message")
        String adminReply,
        
        @Schema(description = "Admin reply timestamp")
        LocalDateTime adminReplyAt,
        
        @Schema(description = "Admin ID who replied", example = "1")
        Long adminId,
        
        @Schema(description = "Admin username", example = "admin")
        String adminUsername,
        
        @Schema(description = "Review creation timestamp")
        LocalDateTime createdAt,
        
        @Schema(description = "Review last update timestamp")
        LocalDateTime updatedAt
) {
}
