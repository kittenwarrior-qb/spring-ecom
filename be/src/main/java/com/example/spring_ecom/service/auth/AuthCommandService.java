package com.example.spring_ecom.service.auth;

import com.example.spring_ecom.config.JwtConfig;
import com.example.spring_ecom.controller.api.auth.model.AuthResponse;
import com.example.spring_ecom.core.util.CookieUtil;
import com.example.spring_ecom.domain.auth.LoginDto;
import com.example.spring_ecom.domain.auth.RegisterDto;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.JwtUtil;
import com.example.spring_ecom.domain.user.UserRole;
import com.example.spring_ecom.repository.database.auth.AuthEntityMapper;
import com.example.spring_ecom.domain.user.UserRole;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.service.auth.email.EmailUseCase;
import com.example.spring_ecom.service.auth.session.RedisSessionService;
import com.example.spring_ecom.service.auth.session.SessionData;

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
    private final JwtConfig jwtConfig;
    private final RedisSessionService redisSessionService;
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
        String sessionId = redisSessionService.createSession(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getRole().name(),
                deviceInfo,
                ipAddress
        );

        // Generate JWT with sessionId
        String accessToken = jwtUtil.generateToken(sessionId);
        
        // Get session data to extract refresh token
        SessionData sessionData = redisSessionService.getSession(sessionId);
        cookieUtil.addRefreshTokenCookie(response, sessionData.getRefreshToken());

        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getRole()
        );
        
        return AuthResponse.of(accessToken, jwtConfig.getExpiration(), userInfo);
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
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getRole()
        );

        // Return a special response indicating email verification is needed
        return AuthResponse.builder()
                .accessToken(null)
                .expiresIn(0L)
                .userInfo(userInfo)
                .message("Registration successful. Please check your email to verify your account.")
                .build();
    }
    
    public AuthResponse refreshToken(String oldRefreshToken, String deviceInfo, String ipAddress, HttpServletResponse response) {
        if (oldRefreshToken == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Refresh token is missing");
        }
        
        // Refresh session in Redis
        SessionData sessionData = redisSessionService.refreshSession(oldRefreshToken, deviceInfo, ipAddress);
        
        // Generate new JWT with new sessionId
        String accessToken = jwtUtil.generateToken(sessionData.getSessionId());
        
        // Set new refresh token cookie
        cookieUtil.addRefreshTokenCookie(response, sessionData.getRefreshToken());
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                sessionData.getUserId(),
                null, // username not stored in session, could be fetched if needed
                sessionData.getEmail(),
                null, // firstName not stored in session
                null, // lastName not stored in session
                UserRole.valueOf(sessionData.getRole())
        );
        
        return AuthResponse.of(accessToken, jwtConfig.getExpiration(), userInfo);
    }
    
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            redisSessionService.revokeRefreshToken(refreshToken);
        }
    }
}
