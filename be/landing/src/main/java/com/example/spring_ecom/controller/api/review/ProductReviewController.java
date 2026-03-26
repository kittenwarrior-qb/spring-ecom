package com.example.spring_ecom.controller.api.review;

import com.example.spring_ecom.controller.api.review.model.CreateReviewRequest;
import com.example.spring_ecom.controller.api.review.model.ProductReviewRequestMapper;
import com.example.spring_ecom.controller.api.review.model.ProductReviewResponse;
import com.example.spring_ecom.controller.api.review.model.ProductReviewResponseMapper;
import com.example.spring_ecom.controller.api.review.model.UpdateReviewRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.review.ProductReview;
import com.example.spring_ecom.service.review.ProductReviewUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductReviewController implements ProductReviewAPI {
    
    private final ProductReviewUseCase reviewUseCase;
    private final ProductReviewRequestMapper requestMapper;
    private final ProductReviewResponseMapper responseMapper;
    
    @Override
    public ApiResponse<Page<ProductReviewResponse>> getReviewsByProductId(Long productId, Pageable pageable) {
        Page<ProductReview> reviews = reviewUseCase.getReviewsByProductId(productId, pageable);
        Page<ProductReviewResponse> responses = reviews.map(responseMapper::toResponse);
        return ApiResponse.Success.of(responses);
    }
    
    @Override
    public ApiResponse<Page<ProductReviewResponse>> getMyReviews(Authentication authentication, Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<ProductReview> reviews = reviewUseCase.getReviewsByUserId(userId, pageable);
        Page<ProductReviewResponse> responses = reviews.map(responseMapper::toResponse);
        return ApiResponse.Success.of(responses);
    }
    
    @Override
    public ApiResponse<ProductReviewResponse> getReviewById(Long id) {
        ProductReview review = reviewUseCase.getReviewById(id);
        return ApiResponse.Success.of(responseMapper.toResponse(review));
    }
    
    @Override
    public ApiResponse<ProductReviewResponse> createReview(@Valid CreateReviewRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        ProductReview reviewRequest = requestMapper.toDomain(userId, request);
        ProductReview review = reviewUseCase.createReview(reviewRequest);
        return ApiResponse.Success.of(ResponseCode.CREATED, "Review created successfully", responseMapper.toResponse(review));
    }
    
    @Override
    public ApiResponse<ProductReviewResponse> updateReview(Long id, @Valid UpdateReviewRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        ProductReview updateRequest = requestMapper.toDomain(request);
        ProductReview review = reviewUseCase.updateReview(id, userId, updateRequest);
        return ApiResponse.Success.of(ResponseCode.OK, "Review updated successfully", responseMapper.toResponse(review));
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
}
