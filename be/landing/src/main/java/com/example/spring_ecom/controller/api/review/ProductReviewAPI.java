package com.example.spring_ecom.controller.api.review;

import com.example.spring_ecom.controller.api.review.model.CreateReviewRequest;
import com.example.spring_ecom.controller.api.review.model.ProductReviewResponse;
import com.example.spring_ecom.controller.api.review.model.UpdateReviewRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product Reviews", description = "Product review management APIs")
@RequestMapping("/api/reviews")
@SecurityRequirement(name = "bearerAuth")
public interface ProductReviewAPI {
    
    @GetMapping("/product/{productId}")
    @Operation(
            summary = "Get reviews by product ID",
            description = "Retrieve all reviews for a specific product with pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved reviews"
            )
    })
    ApiResponse<Page<ProductReviewResponse>> getReviewsByProductId(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId,
            @Parameter(hidden = true) Pageable pageable
    );
    
    @GetMapping("/my-reviews")
    @Operation(
            summary = "Get current user's reviews",
            description = "Retrieve all reviews written by the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved reviews"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<Page<ProductReviewResponse>> getMyReviews(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(hidden = true) Pageable pageable
    );
    
    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    ApiResponse<ProductReviewResponse> getReviewById(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id
    );
    
    @PostMapping
    @Operation(
            summary = "Create a review",
            description = "Create a new product review. User can only review each product once."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Review created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "User has already reviewed this product"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<ProductReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @PutMapping("/{id}")
    @Operation(
            summary = "Update a review",
            description = "Update an existing review. User can only update their own reviews."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Review updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Can only update own reviews"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
    ApiResponse<ProductReviewResponse> updateReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a review",
            description = "Delete a review. User can only delete their own reviews."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Review deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Can only delete own reviews"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
    ApiResponse<Void> deleteReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @PostMapping("/{id}/like")
    @Operation(
            summary = "Like a review",
            description = "Toggle like on a review. If already liked, it will unlike. If disliked, it will switch to like."
    )
    ApiResponse<Void> likeReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @PostMapping("/{id}/dislike")
    @Operation(
            summary = "Dislike a review",
            description = "Toggle dislike on a review. If already disliked, it will remove dislike. If liked, it will switch to dislike."
    )
    ApiResponse<Void> dislikeReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    );
}
