package com.example.spring_ecom.service.auth;

import com.example.spring_ecom.controller.api.auth.model.AuthResponse;
import com.example.spring_ecom.core.util.CookieUtil;
import com.example.spring_ecom.domain.auth.LoginDto;
import com.example.spring_ecom.domain.auth.RegisterDto;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.JwtUtil;
import com.example.spring_ecom.repository.database.auth.AuthEntityMapper;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.service.auth.email.EmailUseCase;
import com.example.spring_ecom.service.auth.session.RedisService;
import com.example.spring_ecom.service.auth.token.TokenInfo;
import com.example.spring_ecom.service.auth.token.TokenService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class AuthCommandService {
    private final UserRepository userRepository;
    private final AuthEntityMapper authEntityMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final EmailUseCase emailUseCase;

    public AuthResponse login(LoginDto command, String deviceInfo, String ipAddress, HttpServletResponse response) {
        UserEntity userEntity = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED, "Wrong email"));
        
        if (!userEntity.getIsActive()) {
            throw new BaseException(ResponseCode.FORBIDDEN, "Account is not activated");
        }

        if (!userEntity.getIsEmailVerified()) {
            throw new BaseException(ResponseCode.FORBIDDEN, "Email is not verified. Please check your email and verify your account.");
        }

        if (!passwordEncoder.matches(command.password(), userEntity.getPassword())) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Wrong password");
        }

        userEntity.setLastLoginAt(LocalDateTime.now());
        userRepository.save(userEntity);

        // Create session in Redis
        String sessionId = redisService.createSession(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getRole().name(),
                deviceInfo,
                ipAddress
        );

        // Generate JWT tokens
        String accessToken = jwtUtil.generateAccessToken(sessionId);
        String refreshToken = jwtUtil.generateRefreshToken(sessionId);
        
        // Set refresh token cookie
        cookieUtil.addRefreshTokenCookie(response, refreshToken);
        
        return AuthResponse.of(accessToken, refreshToken);
    }

    public AuthResponse register(RegisterDto command, String deviceInfo, String ipAddress, HttpServletResponse response) {
        if (userRepository.existsByEmail(command.email())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Email is existed");
        }

        if (userRepository.existsByUsername(command.username())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Username is existed");
        }

        UserEntity userEntity = authEntityMapper.toEntity(command);
        userEntity.setPassword(passwordEncoder.encode(command.password()));

        userEntity = userRepository.save(userEntity);

        // Send email verification
        try {
            emailUseCase.sendVerificationEmail(userEntity.getId());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", userEntity.getEmail(), e.getMessage());
            // Continue with registration even if email fails
        }

        // Return response without tokens - user needs to verify email first
        return AuthResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .build();
    }
    
    public AuthResponse refreshToken(String oldRefreshToken, String deviceInfo, String ipAddress, HttpServletResponse response) {
        if (oldRefreshToken == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Refresh token is missing");
        }
        
        // Validate refresh token and get session info
        TokenInfo tokenInfo = tokenService.validateRefreshToken(oldRefreshToken);
        
        // Create new session
        String newSessionId = redisService.createSession(
                tokenInfo.getUserId(),
                tokenInfo.getEmail(),
                tokenInfo.getRole(),
                deviceInfo,
                ipAddress
        );
        
        // Revoke old session
        redisService.revokeSession(tokenInfo.getSessionId());
        
        // Generate new tokens
        String accessToken = jwtUtil.generateAccessToken(newSessionId);
        String refreshToken = jwtUtil.generateRefreshToken(newSessionId);
        
        // Set new refresh token cookie
        cookieUtil.addRefreshTokenCookie(response, refreshToken);
        
        return AuthResponse.of(accessToken, refreshToken);
    }
    
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            try {
                String sessionId = tokenService.extractSessionId(refreshToken);
                redisService.revokeSession(sessionId);
            } catch (Exception e) {
                log.warn("Failed to revoke session during logout: {}", e.getMessage());
            }
        }
    }
    
    public void logoutBySessionId(String sessionId) {
        if (sessionId != null) {
            redisService.revokeSession(sessionId);
        }
    }
}
