package com.example.spring_ecom.service.review;

import com.example.spring_ecom.domain.review.ProductReview;
import com.example.spring_ecom.repository.database.review.ProductReviewEntityMapper;
import com.example.spring_ecom.repository.database.review.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductReviewQueryService {
    
    private final ProductReviewRepository repository;
    private final ProductReviewEntityMapper mapper;
    
    protected Page<ProductReview> findByProductId(Long productId, Pageable pageable) {
        return repository.findByProductId(productId, pageable)
                .map(mapper::toDomain);
    }
    
    protected Page<ProductReview> findByUserId(Long userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable)
                .map(mapper::toDomain);
    }
    
    protected Optional<ProductReview> findById(Long reviewId) {
        return repository.findByIdAndNotDeleted(reviewId)
                .map(mapper::toDomain);
    }
    
    protected boolean existsByProductIdAndUserId(Long productId, Long userId) {
        return repository.existsByProductIdAndUserIdAndDeletedAtIsNull(productId, userId);
    }
    
    protected Double calculateAverageRating(Long productId) {
        return repository.calculateAverageRating(productId);
    }
    
    protected Long countByProductId(Long productId) {
        return repository.countByProductId(productId);
    }
}
