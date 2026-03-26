package com.example.spring_ecom.domain.review;

import java.time.LocalDateTime;

public record ProductReviewWithUser(
    Long id,
    Long productId,
    Long userId,
    String username,
    Integer rating,
    String title,
    String comment,
    Boolean isVerifiedPurchase,
    Integer likeCount,
    Integer dislikeCount,
    String adminReply,
    LocalDateTime adminReplyAt,
    Long adminId,
    String adminUsername,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}