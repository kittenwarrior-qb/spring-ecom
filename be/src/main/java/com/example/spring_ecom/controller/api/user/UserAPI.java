package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.*;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "User management APIs")
@RequestMapping("/v1/api/user")
@SecurityRequirement(name = "bearerAuth")
public interface UserAPI {

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by user ID")
    ApiResponse<UserResponse> findById(@PathVariable String userId);
    
    @GetMapping("/me")
    @Operation(
            summary = "Get current user profile",
            description = "Retrieve the profile information of the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved profile"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<UserInfoResponse> getCurrentUserProfile(
            @Parameter(hidden = true) Authentication authentication
    );
    
    @PutMapping("/me")
    @Operation(
            summary = "Update user profile",
            description = "Update profile information (name, phone, date of birth, address)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<UserInfoResponse> updateProfile(
            @Valid @RequestBody UserInfoRequest request,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @PutMapping("/me/avatar")
    @Operation(
            summary = "Update user avatar",
            description = "Update the avatar URL of the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Avatar updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid avatar URL"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ApiResponse<UserInfoResponse> updateAvatar(
            @Valid @RequestBody UserInfoRequest request,
            @Parameter(hidden = true) Authentication authentication
    );
    
    @PutMapping("/me/password")
    @Operation(
            summary = "Change password",
            description = "Change the password of the authenticated user. Requires current password for verification."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid password or passwords don't match"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized or incorrect current password"
            )
    })
    ApiResponse<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @Parameter(hidden = true) Authentication authentication
    );

}
