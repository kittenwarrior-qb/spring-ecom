package com.example.spring_ecom.controller.api.auth;

import com.example.spring_ecom.controller.api.auth.model.LoginResponse;
import com.example.spring_ecom.controller.api.auth.model.LoginRequest;
import com.example.spring_ecom.controller.api.auth.model.RegisterRequest;
import com.example.spring_ecom.core.ratelimit.RateLimit;
import com.example.spring_ecom.core.ratelimit.RateLimitType;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;

@Tag(name = "User Authentication", description = "User authentication APIs")
@RequestMapping("/v1/api/auth")
public interface AuthAPI {

    @Operation(summary = "User Login", description = "User login with email and password")
    @PostMapping("/login")
    @RateLimit(limit = 5, duration = 1, unit = ChronoUnit.MINUTES, type = RateLimitType.IP,
               message = "Too many login attempts. Please try again in 1 minute.")
    ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse);

    @Operation(summary = "User Registration", description = "Register new user account")
    @PostMapping("/register")
    @RateLimit(limit = 3, duration = 1, unit = ChronoUnit.MINUTES, type = RateLimitType.IP,
               message = "Too many registration attempts. Please try again in 1 minute.")
    ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse);

    @Operation(summary = "Refresh token", description = "Refresh access token using refresh token from cookie")
    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<LoginResponse>> refresh(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse);

    @Operation(summary = "User Logout", description = "Logout and revoke refresh token")
    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse);

    @Operation(summary = "Forgot Password", description = "Send password reset email")
    @PostMapping("/forgot-password")
    @RateLimit(limit = 3, duration = 5, unit = ChronoUnit.MINUTES, type = RateLimitType.IP,
               message = "Too many password reset attempts. Please try again in 5 minutes.")
    ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email);

    @Operation(summary = "Reset Password", description = "Reset password with token")
    @PostMapping("/reset-password")
    ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword);
}
