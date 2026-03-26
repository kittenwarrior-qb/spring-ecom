package com.example.spring_ecom.controller.api.review;

import com.example.spring_ecom.controller.api.review.model.AdminReplyRequest;
import com.example.spring_ecom.controller.api.review.model.ProductReviewResponse;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - Product Reviews", description = "Admin APIs for managing product reviews")
@RequestMapping("/api/admin/reviews")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ADMIN_ACCESS')")
public interface AdminReviewAPI {
    
    @PostMapping("/{id}/reply")
    @Operation(
            summary = "Add admin reply to a review",
            description = "Admin can reply to customer reviews"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reply added successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
    ApiResponse<ProductReviewResponse> addAdminReply(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id,
            @Valid @RequestBody AdminReplyRequest request,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @DeleteMapping("/{id}/reply")
    @Operation(
            summary = "Delete admin reply",
            description = "Admin can delete their reply from a review"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reply deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
    ApiResponse<Void> deleteAdminReply(
            @Parameter(description = "Review ID", required = true) @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    );
}
