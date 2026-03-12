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
import com.example.spring_ecom.service.auth.redis.RedisServiceWithFallback;
import com.example.spring_ecom.service.auth.token.TokenInfo;
import com.example.spring_ecom.service.auth.token.TokenService;
import com.example.spring_ecom.service.userInfo.UserInfoUseCase;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AuthCommandService {
    private final UserRepository userRepository;
    private final AuthEntityMapper authEntityMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisServiceWithFallback redisService;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final EmailUseCase emailUseCase;
    private final UserInfoUseCase userInfoUseCase;

    public AuthResponse login(LoginDto command, String deviceInfo, String ipAddress, HttpServletResponse response) {
        UserEntity userEntity = validateAndGetUser(command);
        updateLastLogin(userEntity);
        
        String sessionId = createUserSession(userEntity, deviceInfo, ipAddress);
        return generateAuthResponse(sessionId, response);
    }

    public AuthResponse register(RegisterDto command, String deviceInfo, String ipAddress, HttpServletResponse response) {
        validateUserRegistration(command);
        
        UserEntity userEntity = createUser(command);
        sendVerificationEmail(userEntity);
        
        return AuthResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .build();
    }
    
    public AuthResponse refreshToken(String oldRefreshToken, String deviceInfo, String ipAddress, HttpServletResponse response) {
        validateRefreshToken(oldRefreshToken);
        
        TokenInfo tokenInfo = tokenService.validateRefreshToken(oldRefreshToken);
        String newSessionId = createTokenSession(tokenInfo, deviceInfo, ipAddress);
        
        redisService.revokeSession(tokenInfo.getSessionId());
        
        return generateAuthResponse(newSessionId, response);
    }
    
    public void logout(String refreshToken) {
        Optional.ofNullable(refreshToken)
                .filter(token -> !token.trim().isEmpty())
                .ifPresent(token -> {
                    try {
                        String sessionId = tokenService.extractSessionId(token);
                        Optional.ofNullable(sessionId)
                                .filter(id -> !id.trim().isEmpty())
                                .ifPresent(redisService::revokeSession);
                    } catch (Exception e) {
                        log.warn("Failed to revoke session during logout: {}", e.getMessage());
                    }
                });
    }
    
    public void logoutBySessionId(String sessionId) {
        Optional.ofNullable(sessionId)
                .filter(id -> !id.trim().isEmpty())
                .ifPresent(redisService::revokeSession);
    }
    
    private UserEntity validateAndGetUser(LoginDto command) {
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
        
        return userEntity;
    }
    
    private void updateLastLogin(UserEntity userEntity) {
        userEntity.setLastLoginAt(LocalDateTime.now());
        userRepository.save(userEntity);
    }
    
    private String createUserSession(UserEntity userEntity, String deviceInfo, String ipAddress) {
        // Load user info from UserInfo table
        var userInfo = userInfoUseCase.findByUserId(userEntity.getId()).orElse(null);
        
        return redisService.createSession(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getRole().name(),
                userInfo != null ? userInfo.firstName() : null,
                userInfo != null ? userInfo.lastName() : null,
                userInfo != null ? userInfo.phoneNumber() : null,
                userInfo != null ? userInfo.address() : null,
                userInfo != null ? userInfo.city() : null,
                userInfo != null ? userInfo.district() : null,
                userInfo != null ? userInfo.ward() : null,
                Objects.isNull(deviceInfo) ? "Unknown Device" : deviceInfo,
                Objects.isNull(ipAddress) ? "Unknown IP" : ipAddress
        );
    }
    
    private String createTokenSession(TokenInfo tokenInfo, String deviceInfo, String ipAddress) {
        return redisService.createSession(
                tokenInfo.getUserId(),
                tokenInfo.getEmail(),
                tokenInfo.getRole(),
                Objects.isNull(deviceInfo) ? "Unknown Device" : deviceInfo,
                Objects.isNull(ipAddress) ? "Unknown IP" : ipAddress
        );
    }
    
    private AuthResponse generateAuthResponse(String sessionId, HttpServletResponse response) {
        String accessToken = jwtUtil.generateAccessToken(sessionId);
        String refreshToken = jwtUtil.generateRefreshToken(sessionId);
        
        cookieUtil.addRefreshTokenCookie(response, refreshToken);
        
        return AuthResponse.of(accessToken, refreshToken);
    }
    
    private void validateUserRegistration(RegisterDto command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Email is existed");
        }

        if (userRepository.existsByUsername(command.username())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Username is existed");
        }
    }
    
    private UserEntity createUser(RegisterDto command) {
        UserEntity userEntity = authEntityMapper.toEntity(command);
        userEntity.setPassword(passwordEncoder.encode(command.password()));
        return userRepository.save(userEntity);
    }
    
    private void sendVerificationEmail(UserEntity userEntity) {
        try {
            emailUseCase.sendVerificationEmail(userEntity.getId());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", userEntity.getEmail(), e.getMessage());
            // Continue with registration even if email fails
        }
    }
    
    private void validateRefreshToken(String refreshToken) {
        if (Objects.isNull(refreshToken) || refreshToken.trim().isEmpty()) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Refresh token is missing");
        }
    }
}