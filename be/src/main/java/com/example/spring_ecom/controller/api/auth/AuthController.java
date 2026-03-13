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

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    private final AuthUseCase authUseCase;
    private final AuthRequestMapper requestMapper;
    private final CookieUtil cookieUtil;
    
    @Operation(summary = "Login", description = "Login with email and password")
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
    
    @Operation(summary = "Register", description = "Register new user account")
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
    
    @Operation(summary = "Logout", description = "Logout and revoke refresh token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(httpRequest);
        authUseCase.logout(refreshToken);
        cookieUtil.deleteRefreshTokenCookie(httpResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Logged out successfully", null));
    }
}
