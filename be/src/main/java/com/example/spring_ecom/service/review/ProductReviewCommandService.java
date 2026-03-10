package com.example.spring_ecom.service.review;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.review.ProductReview;
import com.example.spring_ecom.repository.database.review.ProductReviewEntity;
import com.example.spring_ecom.repository.database.review.ProductReviewEntityMapper;
import com.example.spring_ecom.repository.database.review.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewCommandService {
    
    private final ProductReviewRepository repository;
    private final ProductReviewEntityMapper mapper;
    
    protected ProductReview create(ProductReview review) {
        ProductReviewEntity entity = mapper.toEntity(review);
        ProductReviewEntity saved = repository.save(entity);
        log.info("Review created successfully with ID: {}", saved.getId());
        return mapper.toDomain(saved);
    }
    
    protected ProductReview update(Long reviewId, Long userId, Integer rating, String title, String comment) {
        ProductReviewEntity entity = repository.findByIdAndNotDeleted(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
        
        // Verify ownership
        if (!entity.getUserId().equals(userId)) {
            throw new BaseException(ResponseCode.FORBIDDEN, "You can only update your own reviews");
        }
        
        entity.setRating(rating);
        entity.setTitle(title);
        entity.setComment(comment);
        
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected void softDelete(Long reviewId, Long userId) {
        ProductReviewEntity entity = repository.findByIdAndNotDeleted(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
        
        // Verify ownership
        if (!entity.getUserId().equals(userId)) {
            throw new BaseException(ResponseCode.FORBIDDEN, "You can only delete your own reviews");
        }
        
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
        log.info("Review soft deleted: {}", reviewId);
    }
    
    protected ProductReview addAdminReply(Long reviewId, Long adminId, String reply) {
        ProductReviewEntity entity = repository.findByIdAndNotDeleted(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
        
        entity.setAdminReply(reply);
        entity.setAdminReplyAt(LocalDateTime.now());
        entity.setAdminId(adminId);
        
        ProductReviewEntity updated = repository.save(entity);
        log.info("Admin reply added to review: {}", reviewId);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview deleteAdminReply(Long reviewId, Long adminId) {
        ProductReviewEntity entity = repository.findByIdAndNotDeleted(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
        
        entity.setAdminReply(null);
        entity.setAdminReplyAt(null);
        entity.setAdminId(null);
        
        ProductReviewEntity updated = repository.save(entity);
        log.info("Admin reply deleted from review: {}", reviewId);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview incrementHelpfulCount(Long reviewId) {
        ProductReviewEntity entity = repository.findByIdAndNotDeleted(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
        
        entity.setLikeCount(entity.getLikeCount() + 1);
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview incrementLikeCount(Long reviewId) {
        ProductReviewEntity entity = repository.findByIdAndNotDeleted(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
        
        entity.setLikeCount(entity.getLikeCount() + 1);
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview decrementLikeCount(Long reviewId) {
        ProductReviewEntity entity = repository.findByIdAndNotDeleted(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
        
        entity.setLikeCount(Math.max(0, entity.getLikeCount() - 1));
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview incrementDislikeCount(Long reviewId) {
        ProductReviewEntity entity = repository.findByIdAndNotDeleted(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
        
        entity.setDislikeCount(entity.getDislikeCount() + 1);
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview decrementDislikeCount(Long reviewId) {
        ProductReviewEntity entity = repository.findByIdAndNotDeleted(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
        
        entity.setDislikeCount(Math.max(0, entity.getDislikeCount() - 1));
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
}
