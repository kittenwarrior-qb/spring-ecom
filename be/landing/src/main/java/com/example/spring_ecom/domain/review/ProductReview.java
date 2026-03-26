package com.example.spring_ecom.domain.review;

import java.time.LocalDateTime;

public record ProductReview(
        Long id,
        Long productId,
        Long userId,
        Integer rating,
        String title,
        String comment,
        Boolean isVerifiedPurchase,
        Integer likeCount,
        Integer dislikeCount,
        String adminReply,
        LocalDateTime adminReplyAt,
        Long adminId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
