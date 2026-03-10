package com.example.spring_ecom.controller.api.review;

import com.example.spring_ecom.controller.api.review.model.AdminReplyRequest;
import com.example.spring_ecom.controller.api.review.model.ProductReviewResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.review.ProductReview;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.service.review.ProductReviewUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminReviewController implements AdminReviewAPI {
    
    private final ProductReviewUseCase reviewUseCase;
    private final UserRepository userRepository;
    
    @Override
    public ApiResponse<ProductReviewResponse> addAdminReply(Long id, @Valid AdminReplyRequest request, Authentication authentication) {
        Long adminId = SecurityUtil.getCurrentUserId();
        ProductReview review = reviewUseCase.addAdminReply(id, adminId, request.reply());
        return ApiResponse.Success.of(ResponseCode.OK, "Admin reply added successfully", toResponse(review));
    }
    
    @Override
    public ApiResponse<Void> deleteAdminReply(Long id, Authentication authentication) {
        Long adminId = SecurityUtil.getCurrentUserId();
        reviewUseCase.deleteAdminReply(id, adminId);
        return ApiResponse.Success.of(ResponseCode.OK, "Admin reply deleted successfully");
    }
    
    private ProductReviewResponse toResponse(ProductReview review) {
        String username = userRepository.findById(review.userId())
                .map(UserEntity::getUsername)
                .orElse("Unknown");
        
        String adminUsername = review.adminId() != null ?
                userRepository.findById(review.adminId())
                        .map(UserEntity::getUsername)
                        .orElse("Unknown") : null;
        
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
}
