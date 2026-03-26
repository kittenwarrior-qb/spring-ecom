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

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewCommandService {
    
    private final ProductReviewRepository repository;
    private final ProductReviewEntityMapper mapper;
    
    // ========================== MAIN METHODS ================================
    
    protected ProductReview create(ProductReview review) {
        ProductReviewEntity entity = mapper.toEntity(review);
        ProductReviewEntity saved = repository.save(entity);
        log.info("Review created successfully with ID: {}", saved.getId());
        return mapper.toDomain(saved);
    }
    
    protected ProductReview update(Long reviewId, Long userId, ProductReview updateRequest) {
        ProductReviewEntity entity = findReviewByIdAndUserId(reviewId, userId);
        
        mapper.updateFromDomain(entity, updateRequest);
        
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected void softDelete(Long reviewId, Long userId) {
        ProductReviewEntity entity = findReviewByIdAndUserId(reviewId, userId);
        
        mapper.markAsDeleted(entity, null);
        repository.save(entity);
        log.info("Review soft deleted: {}", reviewId);
    }
    
    protected ProductReview addAdminReply(Long reviewId, Long adminId, String reply) {
        ProductReviewEntity entity = findActiveReviewById(reviewId);
        
        mapper.addAdminReply(entity, adminId, reply);
        
        ProductReviewEntity updated = repository.save(entity);
        log.info("Admin reply added to review: {}", reviewId);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview deleteAdminReply(Long reviewId, Long adminId) {
        ProductReviewEntity entity = findActiveReviewById(reviewId);
        
        mapper.removeAdminReply(entity, null);
        
        ProductReviewEntity updated = repository.save(entity);
        log.info("Admin reply deleted from review: {}", reviewId);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview incrementLikeCount(Long reviewId) {
        ProductReviewEntity entity = findActiveReviewById(reviewId);
        
        mapper.incrementLikeCount(entity, null);
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview decrementLikeCount(Long reviewId) {
        ProductReviewEntity entity = findActiveReviewById(reviewId);
        
        mapper.decrementLikeCount(entity, null);
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview incrementDislikeCount(Long reviewId) {
        ProductReviewEntity entity = findActiveReviewById(reviewId);
        
        mapper.incrementDislikeCount(entity, null);
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected ProductReview decrementDislikeCount(Long reviewId) {
        ProductReviewEntity entity = findActiveReviewById(reviewId);
        
        mapper.decrementDislikeCount(entity, null);
        ProductReviewEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    // ========================== SUPPORT METHODS ================================
    
    private ProductReviewEntity findActiveReviewById(Long reviewId) {
        return repository.findByIdAndNotDeleted(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
    }
    
    private ProductReviewEntity findReviewByIdAndUserId(Long reviewId, Long userId) {
        ProductReviewEntity entity = findActiveReviewById(reviewId);
        
        if (!entity.getUserId().equals(userId)) {
            throw new BaseException(ResponseCode.FORBIDDEN, "You can only modify your own reviews");
        }
        
        return entity;
    }
}
