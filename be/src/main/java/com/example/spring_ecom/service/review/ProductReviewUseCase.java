package com.example.spring_ecom.service.review;

import com.example.spring_ecom.domain.review.ProductReview;
import com.example.spring_ecom.repository.database.review.ReviewReactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductReviewUseCase {
    
    Page<ProductReview> getReviewsByProductId(Long productId, Pageable pageable);
    
    Page<ProductReview> getReviewsByUserId(Long userId, Pageable pageable);
    
    ProductReview getReviewById(Long reviewId);
    
    ProductReview createReview(Long userId, Long productId, Integer rating, String title, String comment);
    
    ProductReview updateReview(Long reviewId, Long userId, Integer rating, String title, String comment);
    
    void deleteReview(Long reviewId, Long userId);
    
    ProductReview addAdminReply(Long reviewId, Long adminId, String reply);
    
    void deleteAdminReply(Long reviewId, Long adminId);
    
    ProductReview toggleReaction(Long reviewId, Long userId, ReviewReactionEntity.ReactionType reactionType);
    
    void removeReaction(Long reviewId, Long userId);
}
