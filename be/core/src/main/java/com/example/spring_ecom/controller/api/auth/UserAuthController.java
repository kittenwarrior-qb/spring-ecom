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
@RequiredArgsConstructor
public class UserAuthController implements AuthAPI {
    
    private final AuthUseCase authUseCase;
    private final AuthRequestMapper requestMapper;
    private final CookieUtil cookieUtil;
    
    @Override
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        LoginResponse response = authUseCase.login(requestMapper.toDomain(request), httpRequest, httpResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }
    
    @Override
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        LoginResponse response = authUseCase.register(requestMapper.toDomain(request), httpRequest, httpResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }
    
    @Override
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(httpRequest);
        LoginResponse response = authUseCase.refreshToken(refreshToken, httpRequest, httpResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(response));
    }
    
    @Override
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(httpRequest);
        authUseCase.logout(refreshToken);
        cookieUtil.deleteRefreshTokenCookie(httpResponse);
        return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Logged out successfully", null));
    }
    
    @Override
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        // TODO: Implement forgot password via gRPC call to server
        return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Password reset email sent", null));
    }
    
    @Override
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        // TODO: Implement reset password via gRPC call to server
        return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Password reset successfully", null));
    }
}
