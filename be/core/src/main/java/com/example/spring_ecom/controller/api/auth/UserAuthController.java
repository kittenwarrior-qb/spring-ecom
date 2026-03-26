package com.example.spring_ecom.controller.api.auth;

import com.example.spring_ecom.controller.api.auth.model.AuthRequestMapper;
import com.example.spring_ecom.controller.api.auth.model.LoginResponse;
import com.example.spring_ecom.controller.api.auth.model.LoginRequest;
import com.example.spring_ecom.controller.api.auth.model.RegisterRequest;
import com.example.spring_ecom.core.ratelimit.RateLimit;
import com.example.spring_ecom.core.ratelimit.RateLimitType;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.CookieUtil;
import com.example.spring_ecom.service.auth.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;

/**
 * User Authentication Controller
 * CLIENT SERVICE - APIs cho người dùng cuối
 */
@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
@Tag(name = "User Authentication", description = "User authentication APIs")
public class UserAuthController {
    
    private final AuthUseCase authUseCase;
    private final AuthRequestMapper requestMapper;
    private final CookieUtil cookieUtil;
    
    @Operation(summary = "User Login", description = "User login with email and password")
    @PostMapping("/login")
    @RateLimit(limit = 5, duration = 1, unit = ChronoUnit.MINUTES, type = RateLimitType.IP, 
               message = "Too many login attempts. Please try again in 1 minute.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        LoginResponse response = authUseCase.login(requestMapper.toDomain(request), httpRequest, httpResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }
    
    @Operation(summary = "User Registration", description = "Register new user account")
    @PostMapping("/register")
    @RateLimit(limit = 3, duration = 1, unit = ChronoUnit.MINUTES, type = RateLimitType.IP,
               message = "Too many registration attempts. Please try again in 1 minute.")
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        LoginResponse response = authUseCase.register(requestMapper.toDomain(request), httpRequest, httpResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }
    
    @Operation(summary = "Refresh token", description = "Refresh access token using refresh token from cookie")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(httpRequest);
        LoginResponse response = authUseCase.refreshToken(refreshToken, httpRequest, httpResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }
    
    @Operation(summary = "User Logout", description = "Logout and revoke refresh token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(httpRequest);
        authUseCase.logout(refreshToken);
        cookieUtil.deleteRefreshTokenCookie(httpResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Logged out successfully", null));
    }
    
    @Operation(summary = "Forgot Password", description = "Send password reset email")
    @PostMapping("/forgot-password")
    @RateLimit(limit = 3, duration = 5, unit = ChronoUnit.MINUTES, type = RateLimitType.IP,
               message = "Too many password reset attempts. Please try again in 5 minutes.")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        // TODO: Implement forgot password via gRPC call to server
        return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Password reset email sent", null));
    }
    
    @Operation(summary = "Reset Password", description = "Reset password with token")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        // TODO: Implement reset password via gRPC call to server
        return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Password reset successfully", null));
    }
}
