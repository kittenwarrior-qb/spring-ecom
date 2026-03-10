package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.*;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.service.user.UserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {
    private final UserUseCase userUseCase;
    private final UserResponseMapper responseMapper;

    @Override
    public ApiResponse<UserResponse> findById(String userId) {
        UserResponse response = userUseCase.findByUserId(Long.parseLong(userId))
                .map(responseMapper::toResDto)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        return ApiResponse.Success.of(response);
    }
    
    @Override
    public ApiResponse<UserProfileResponse> getCurrentUserProfile(Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userUseCase.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        return ApiResponse.Success.of(toProfileResponse(user));
    }
    
    @Override
    public ApiResponse<UserProfileResponse> updateProfile(@Valid UpdateProfileRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        User updated = userUseCase.updateProfile(
                userId,
                request.firstName(),
                request.lastName(),
                request.phoneNumber(),
                request.dateOfBirth()
        );
        return ApiResponse.Success.of(ResponseCode.OK, "Profile updated successfully", toProfileResponse(updated));
    }
    
    @Override
    public ApiResponse<UserProfileResponse> updateAvatar(@Valid UpdateAvatarRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        User updated = userUseCase.updateAvatar(userId, request.avatarUrl());
        return ApiResponse.Success.of(ResponseCode.OK, "Avatar updated successfully", toProfileResponse(updated));
    }
    
    @Override
    public ApiResponse<Void> changePassword(@Valid ChangePasswordRequest request, Authentication authentication) {
        // Validate passwords match
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "New password and confirm password do not match");
        }
        
        Long userId = SecurityUtil.getCurrentUserId();
        userUseCase.changePassword(userId, request.currentPassword(), request.newPassword());
        return ApiResponse.Success.of(ResponseCode.OK, "Password changed successfully");
    }
    
    private UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
                user.id(),
                user.username(),
                user.email(),
                user.firstName(),
                user.lastName(),
                user.phoneNumber(),
                user.dateOfBirth(),
                user.avatarUrl(),
                user.role(),
                user.isEmailVerified(),
                user.isActive(),
                user.lastLoginAt(),
                user.createdAt(),
                user.updatedAt()
        );
    }
}
