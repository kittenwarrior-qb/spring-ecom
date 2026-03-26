package com.example.spring_ecom.service.review;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.review.ProductReview;
import com.example.spring_ecom.repository.database.review.ReviewReactionEntity;
import com.example.spring_ecom.repository.database.review.ReviewReactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewUseCaseService implements ProductReviewUseCase {
    
    private final ProductReviewQueryService queryService;
    private final ProductReviewCommandService commandService;
    private final ReviewReactionRepository reactionRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductReview> getReviewsByProductId(Long productId, Pageable pageable) {
        return queryService.findByProductId(productId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductReview> getReviewsByUserId(Long userId, Pageable pageable) {
        return queryService.findByUserId(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductReview getReviewById(Long reviewId) {
        return queryService.findById(reviewId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
    }
    
    @Override
    @Transactional
    public ProductReview createReview(ProductReview review) {
        // Check if user already reviewed this product
        if (queryService.existsByProductIdAndUserId(review.productId(), review.userId())) {
            throw new BaseException(ResponseCode.CONFLICT, "You have already reviewed this product");
        }
        
        return commandService.create(review);
    }
    
    @Override
    @Transactional
    public ProductReview updateReview(Long reviewId, Long userId, ProductReview updateRequest) {
        return commandService.update(reviewId, userId, updateRequest);
    }
    
    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        commandService.softDelete(reviewId, userId);
    }
    
    @Override
    @Transactional
    public ProductReview addAdminReply(Long reviewId, Long adminId, String reply) {
        return commandService.addAdminReply(reviewId, adminId, reply);
    }
    
    @Override
    @Transactional
    public void deleteAdminReply(Long reviewId, Long adminId) {
        commandService.deleteAdminReply(reviewId, adminId);
    }
    
    @Override
    @Transactional
    public ProductReview toggleReaction(Long reviewId, Long userId, ReviewReactionEntity.ReactionType reactionType) {
        Optional<ReviewReactionEntity> existingReaction = reactionRepository.findByReviewIdAndUserId(reviewId, userId);
        
        if (existingReaction.isPresent()) {
            ReviewReactionEntity reaction = existingReaction.get();
            
            if (reaction.getReactionType() == reactionType) {
                // Same reaction - remove it (toggle off)
                reactionRepository.delete(reaction);
                if (reactionType == ReviewReactionEntity.ReactionType.LIKE) {
                    return commandService.decrementLikeCount(reviewId);
                } else {
                    return commandService.decrementDislikeCount(reviewId);
                }
            } else {
                // Different reaction - switch it
                if (reaction.getReactionType() == ReviewReactionEntity.ReactionType.LIKE) {
                    commandService.decrementLikeCount(reviewId);
                    commandService.incrementDislikeCount(reviewId);
                } else {
                    commandService.decrementDislikeCount(reviewId);
                    commandService.incrementLikeCount(reviewId);
                }
                reaction.setReactionType(reactionType);
                reactionRepository.save(reaction);
                return queryService.findById(reviewId)
                        .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Review not found"));
            }
        } else {
            // New reaction
            ReviewReactionEntity newReaction = ReviewReactionEntity.builder()
                    .reviewId(reviewId)
                    .userId(userId)
                    .reactionType(reactionType)
                    .build();
            reactionRepository.save(newReaction);
            
            if (reactionType == ReviewReactionEntity.ReactionType.LIKE) {
                return commandService.incrementLikeCount(reviewId);
            } else {
                return commandService.incrementDislikeCount(reviewId);
            }
        }
    }
    
    @Override
    @Transactional
    public void removeReaction(Long reviewId, Long userId) {
        Optional<ReviewReactionEntity> reaction = reactionRepository.findByReviewIdAndUserId(reviewId, userId);
        if (reaction.isPresent()) {
            ReviewReactionEntity.ReactionType type = reaction.get().getReactionType();
            reactionRepository.delete(reaction.get());
            
            if (type == ReviewReactionEntity.ReactionType.LIKE) {
                commandService.decrementLikeCount(reviewId);
            } else {
                commandService.decrementDislikeCount(reviewId);
            }
        }
    }
}
