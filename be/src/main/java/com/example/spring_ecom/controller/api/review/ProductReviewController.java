package com.example.spring_ecom.controller.api.review;

import com.example.spring_ecom.controller.api.review.model.CreateReviewRequest;
import com.example.spring_ecom.controller.api.review.model.ProductReviewResponse;
import com.example.spring_ecom.controller.api.review.model.UpdateReviewRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.review.ProductReview;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.service.review.ProductReviewUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductReviewController implements ProductReviewAPI {
    
    private final ProductReviewUseCase reviewUseCase;
    private final UserRepository userRepository;
    
    @Override
    public ApiResponse<Page<ProductReviewResponse>> getReviewsByProductId(Long productId, Pageable pageable) {
        Page<ProductReview> reviews = reviewUseCase.getReviewsByProductId(productId, pageable);
        Page<ProductReviewResponse> responses = reviews.map(this::toResponse);
        return ApiResponse.Success.of(responses);
    }
    
    @Override
    public ApiResponse<Page<ProductReviewResponse>> getMyReviews(Authentication authentication, Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<ProductReview> reviews = reviewUseCase.getReviewsByUserId(userId, pageable);
        Page<ProductReviewResponse> responses = reviews.map(this::toResponse);
        return ApiResponse.Success.of(responses);
    }
    
    @Override
    public ApiResponse<ProductReviewResponse> getReviewById(Long id) {
        ProductReview review = reviewUseCase.getReviewById(id);
        return ApiResponse.Success.of(toResponse(review));
    }
    
    @Override
    public ApiResponse<ProductReviewResponse> createReview(@Valid CreateReviewRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        ProductReview review = reviewUseCase.createReview(
                userId,
                request.productId(),
                request.rating(),
                request.title(),
                request.comment()
        );
        return ApiResponse.Success.of(ResponseCode.CREATED, "Review created successfully", toResponse(review));
    }
    
    @Override
    public ApiResponse<ProductReviewResponse> updateReview(Long id, @Valid UpdateReviewRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        ProductReview review = reviewUseCase.updateReview(
                id,
                userId,
                request.rating(),
                request.title(),
                request.comment()
        );
        return ApiResponse.Success.of(ResponseCode.OK, "Review updated successfully", toResponse(review));
    }
    
    @Override
    public ApiResponse<Void> deleteReview(Long id, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        reviewUseCase.deleteReview(id, userId);
        return ApiResponse.Success.of(ResponseCode.OK, "Review deleted successfully");
    }
    
    @Override
    public ApiResponse<Void> likeReview(Long id, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        reviewUseCase.toggleReaction(id, userId, com.example.spring_ecom.repository.database.review.ReviewReactionEntity.ReactionType.LIKE);
        return ApiResponse.Success.of(ResponseCode.OK, "Review liked");
    }
    
    @Override
    public ApiResponse<Void> dislikeReview(Long id, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        reviewUseCase.toggleReaction(id, userId, com.example.spring_ecom.repository.database.review.ReviewReactionEntity.ReactionType.DISLIKE);
        return ApiResponse.Success.of(ResponseCode.OK, "Review disliked");
    }
    
    private ProductReviewResponse toResponse(ProductReview review) {
        Map<Long, String> usernameCache = new HashMap<>();
        
        String username = getUsernameFromCache(review.userId(), usernameCache);
        String adminUsername = review.adminId() != null ? 
                getUsernameFromCache(review.adminId(), usernameCache) : null;
        
        return new ProductReviewResponse(
                review.id(),
                review.productId(),
                review.userId(),
                username,
                review.rating(),
                review.title(),
                review.comment(),
                review.isVerifiedPurchase(),
                review.likeCount(),
                review.dislikeCount(),
                review.adminReply(),
                review.adminReplyAt(),
                review.adminId(),
                adminUsername,
                review.createdAt(),
                review.updatedAt()
        );
    }
    
    private String getUsernameFromCache(Long userId, Map<Long, String> cache) {
        return cache.computeIfAbsent(userId, id -> 
                userRepository.findById(id)
                        .map(UserEntity::getUsername)
                        .orElse("Unknown")
        );
    }
}
