package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.*;
import com.example.spring_ecom.controller.api.user.userInfo.model.UserInfoRequest;
import com.example.spring_ecom.controller.api.user.userInfo.model.UserInfoRequestMapper;
import com.example.spring_ecom.controller.api.user.userInfo.model.UserInfoResponse;
import com.example.spring_ecom.controller.api.user.userInfo.model.UserInfoResponseMapper;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.ratelimit.RateLimit;
import com.example.spring_ecom.core.ratelimit.RateLimitType;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.repository.redis.session.RedisEntity;
import com.example.spring_ecom.service.auth.redis.RedisService;
import com.example.spring_ecom.service.user.UserUseCase;
import com.example.spring_ecom.service.userInfo.UserInfoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.time.temporal.ChronoUnit;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {
    private final UserUseCase userUseCase;
    private final UserInfoUseCase userInfoUseCase;
    private final UserResponseMapper responseMapper;
    private final UserInfoResponseMapper userInfoResponseMapper;
    private final UserInfoRequestMapper userInfoRequestMapper;
    private final RedisService redisService;

    @Override
    public ApiResponse<UserResponse> findById(String userId) {
        UserResponse response = userUseCase.findByUserId(Long.parseLong(userId))
                .map(responseMapper::toResponse)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        return ApiResponse.Success.of(response);
    }
    
    @Override
    public ApiResponse<UserInfoResponse> getCurrentUserProfile(Authentication authentication) {
        String sessionId = SecurityUtil.getCurrentSessionId();
        RedisEntity session = redisService.validateSession(sessionId);
        
        UserInfoResponse response = userInfoResponseMapper.sessionToResponse(session);
        return ApiResponse.Success.of(response);
    }
    
    @Override
    @RateLimit(type = RateLimitType.USER, limit = 10, duration = 1, unit = ChronoUnit.HOURS,
               message = "Too many profile update attempts. Please try again later.")
    public ApiResponse<UserInfoResponse> updateProfile(@Valid UserInfoRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        userInfoUseCase.createOrUpdate(userId, userInfoRequestMapper.toDomain(userId, request));
        
        User user = userUseCase.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        
        return ApiResponse.Success.of(ResponseCode.OK, "Profile updated successfully", userInfoResponseMapper.toResponse(user));
    }
    
    @Override
    public ApiResponse<UserInfoResponse> updateAvatar(@Valid UserInfoRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        userInfoUseCase.createOrUpdate(userId, userInfoRequestMapper.toDomain(userId, request));
        
        User user = userUseCase.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        
        return ApiResponse.Success.of(ResponseCode.OK, "Avatar updated successfully", userInfoResponseMapper.toResponse(user));
    }
    
    @Override
    @RateLimit(type = RateLimitType.USER, limit = 5, duration = 1, unit = ChronoUnit.HOURS,
               message = "Too many password change attempts. Please try again later.")
    public ApiResponse<Void> changePassword(@Valid ChangePasswordRequest request, Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        userUseCase.changePassword(userId, request.currentPassword(), request.newPassword(), request.confirmPassword());
        return ApiResponse.Success.of(ResponseCode.OK, "Password changed successfully");
    }

}