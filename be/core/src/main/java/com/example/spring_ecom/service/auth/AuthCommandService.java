package com.example.spring_ecom.service.auth;

import com.example.spring_ecom.controller.api.auth.model.LoginResponse;
import com.example.spring_ecom.core.util.CookieUtil;
import com.example.spring_ecom.domain.auth.LoginDto;
import com.example.spring_ecom.domain.auth.RegisterDto;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.JwtUtil;
import com.example.spring_ecom.domain.role.RoleDto;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.kafka.service.UserKafkaProducer;
import com.example.spring_ecom.kafka.domain.UserEvent;

import com.example.spring_ecom.service.auth.redis.RedisService;
import com.example.spring_ecom.service.auth.token.TokenInfo;
import com.example.spring_ecom.service.auth.token.TokenService;
import com.example.spring_ecom.service.role.RoleUseCase;
import com.example.spring_ecom.service.user.UserUseCase;
import com.example.spring_ecom.service.userInfo.UserInfoUseCase;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AuthCommandService {
    private final UserUseCase userUseCase;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final UserKafkaProducer userKafkaProducer;
    private final UserInfoUseCase userInfoUseCase;
    private final RoleUseCase roleUseCase;

    protected LoginResponse login(LoginDto command, String deviceInfo, String ipAddress, HttpServletResponse response) {
        User user = validateAndGetUser(command);
        userUseCase.updateLastLogin(user.id());

        String sessionId = createUserSession(user, deviceInfo, ipAddress);
        return generateLoginResponse(sessionId, user, response);
    }

    protected LoginResponse register(RegisterDto command, String deviceInfo, String ipAddress, HttpServletResponse response) {
        validateUserRegistration(command);
        
        User user = createUser(command);
        sendVerificationEmail(user);

        return new LoginResponse(null, null, null, null);
    }
    
    protected LoginResponse refreshToken(String oldRefreshToken, String deviceInfo, String ipAddress, HttpServletResponse response) {
        validateRefreshToken(oldRefreshToken);
        
        TokenInfo tokenInfo = tokenService.validateRefreshToken(oldRefreshToken);
        User user = userUseCase.findByUserId(tokenInfo.getUserId())
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED, "User not found"));
        
        String newSessionId = createTokenSession(tokenInfo, deviceInfo, ipAddress);
        
        redisService.revokeSession(tokenInfo.getSessionId());
        
        return generateLoginResponse(newSessionId, user, response);
    }
    
    protected void logout(String refreshToken) {
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
    
    protected void logoutBySessionId(String sessionId) {
        Optional.ofNullable(sessionId)
                .filter(id -> !id.trim().isEmpty())
                .ifPresent(redisService::revokeSession);
    }
    
    private User validateAndGetUser(LoginDto command) {
        User user = userUseCase.findByEmail(command.email())
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED, "Wrong email"));
        
        if (!user.isActive()) {
            throw new BaseException(ResponseCode.FORBIDDEN, "Account is not activated");
        }

        if (!user.isEmailVerified()) {
            throw new BaseException(ResponseCode.FORBIDDEN, "Email is not verified. Please check your email and verify your account.");
        }

        if (!passwordEncoder.matches(command.password(), user.password())) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Wrong password");
        }
        
        return user;
    }
    
    private String createUserSession(User user, String deviceInfo, String ipAddress) {
        var userInfo = userInfoUseCase.findByUserId(user.id()).orElse(null);

        // Get roles via RoleUseCase
        List<RoleDto> roles = roleUseCase.getUserRoles(user.id());
        String roleName = roles.isEmpty() ? "USER" : roles.get(0).name();

        // Load authorities (roles + permissions) via RoleUseCase
        String authorities = roleUseCase.buildAuthoritiesString(user.id());

        return redisService.createSession(
                user.id(),
                user.username(),
                user.email(),
                roleName,
                Objects.nonNull(userInfo) ? userInfo.firstName() : null,
                Objects.nonNull(userInfo) ? userInfo.lastName() : null,
                Objects.nonNull(userInfo) ? userInfo.phoneNumber() : null,
                Objects.nonNull(userInfo) ? userInfo.address() : null,
                Objects.nonNull(userInfo) ? userInfo.city() : null,
                Objects.nonNull(userInfo) ? userInfo.district() : null,
                Objects.nonNull(userInfo) ? userInfo.ward() : null,
                Objects.isNull(deviceInfo) ? "Unknown Device" : deviceInfo,
                Objects.isNull(ipAddress) ? "Unknown IP" : ipAddress,
                authorities
        );
    }
    
    private String createTokenSession(TokenInfo tokenInfo, String deviceInfo, String ipAddress) {
        // Reuse authorities from existing token if available
        String authorities = Objects.nonNull(tokenInfo.getAuthorities()) ?
                String.join(",", tokenInfo.getAuthorities()) : null;
        
        return redisService.createSession(
                tokenInfo.getUserId(),
                tokenInfo.getEmail(),
                tokenInfo.getRole(),
                Objects.isNull(deviceInfo) ? "Unknown Device" : deviceInfo,
                Objects.isNull(ipAddress) ? "Unknown IP" : ipAddress,
                authorities
        );
    }
    
    private LoginResponse generateLoginResponse(String sessionId, User user, HttpServletResponse response) {
        String accessToken = jwtUtil.generateAccessToken(sessionId);
        String refreshToken = jwtUtil.generateRefreshToken(sessionId);
        
        cookieUtil.addRefreshTokenCookie(response, refreshToken);
        
        return new LoginResponse(accessToken, refreshToken, user.username(), user.email());
    }
    
    private void validateUserRegistration(RegisterDto command) {
        if (userUseCase.existsByEmail(command.email())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Email is existed");
        }

        if (userUseCase.existsByUsername(command.username())) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Username is existed");
        }
    }
    
    private User createUser(RegisterDto command) {
        String encodedPassword = passwordEncoder.encode(command.password());
        User user = userUseCase.createUserForRegistration(command.username(), command.email(), encodedPassword);

        Long userRoleId = roleUseCase.findRoleIdByName("USER")
                .orElseThrow(() -> new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Default role not found"));
        
        roleUseCase.addRoleToUser(user.id(), userRoleId);

        return user;
    }
    
    private void sendVerificationEmail(User user) {
        try {
            UserEvent event = UserEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(UserEvent.REGISTERED)
                    .timestamp(java.time.Instant.now())
                    .source("server")
                    .userId(user.id())
                    .username(user.username())
                    .email(user.email())
                    .build();
            userKafkaProducer.send(event);
            log.info("Published UserRegisteredEvent to Kafka for userId: {}", user.id());
        } catch (Exception e) {
            log.error("Failed to publish UserRegisteredEvent to Kafka for email {}: {}", user.email(), e.getMessage());
            // Continue with registration even if event pushing fails
        }
    }
    
    private void validateRefreshToken(String refreshToken) {
        if (Objects.isNull(refreshToken) || refreshToken.trim().isEmpty()) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Refresh token is missing");
        }
    }
}

