package com.example.spring_ecom.service.auth;

import com.example.spring_ecom.controller.api.auth.model.LoginResponse;
import com.example.spring_ecom.core.util.CookieUtil;
import com.example.spring_ecom.domain.auth.LoginDto;
import com.example.spring_ecom.domain.auth.RegisterDto;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.JwtUtil;
import com.example.spring_ecom.repository.database.auth.AuthEntityMapper;
import com.example.spring_ecom.repository.database.role.RoleRepository;
import com.example.spring_ecom.repository.database.permission.PermissionEntity;
import com.example.spring_ecom.repository.database.permission.PermissionRepository;
import com.example.spring_ecom.repository.database.role.RolePermissionRepository;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.repository.database.user.UserRoleEntity;
import com.example.spring_ecom.repository.database.user.UserRoleRepository;
import com.example.spring_ecom.kafka.service.UserKafkaProducer;
import com.example.spring_ecom.kafka.domain.UserEvent;

import com.example.spring_ecom.service.auth.redis.RedisService;
import com.example.spring_ecom.service.auth.token.TokenInfo;
import com.example.spring_ecom.service.auth.token.TokenService;
import com.example.spring_ecom.service.userInfo.UserInfoUseCase;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final UserKafkaProducer userKafkaProducer;
    private final UserInfoUseCase userInfoUseCase;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    public LoginResponse login(LoginDto command, String deviceInfo, String ipAddress, HttpServletResponse response) {
        UserEntity userEntity = validateAndGetUser(command);
        updateLastLogin(userEntity);
        
        String sessionId = createUserSession(userEntity, deviceInfo, ipAddress);
        return generateLoginResponse(sessionId, userEntity, response);
    }

    public LoginResponse register(RegisterDto command, String deviceInfo, String ipAddress, HttpServletResponse response) {
        validateUserRegistration(command);
        
        UserEntity userEntity = createUser(command);
        sendVerificationEmail(userEntity);
        
        return new LoginResponse(null, null, null, null);
    }
    
    public LoginResponse refreshToken(String oldRefreshToken, String deviceInfo, String ipAddress, HttpServletResponse response) {
        validateRefreshToken(oldRefreshToken);
        
        TokenInfo tokenInfo = tokenService.validateRefreshToken(oldRefreshToken);
        UserEntity userEntity = userRepository.findById(tokenInfo.getUserId())
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED, "User not found"));
        
        String newSessionId = createTokenSession(tokenInfo, deviceInfo, ipAddress);
        
        redisService.revokeSession(tokenInfo.getSessionId());
        
        return generateLoginResponse(newSessionId, userEntity, response);
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
        var userInfo = userInfoUseCase.findByUserId(userEntity.getId()).orElse(null);
        
        // Get roles from user_roles table instead of role_id column
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userEntity.getId());
        String roleName = roleIds.isEmpty() ? "USER" : 
                roleRepository.findById(roleIds.get(0)).map(r -> r.getName()).orElse("USER");
        
        // Load authorities (roles + permissions)
        String authorities = buildAuthoritiesString(userEntity.getId(), roleIds);
        
        return redisService.createSession(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
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
        String authorities = tokenInfo.getAuthorities() != null ? 
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
    
    /**
     * Build comma-separated authorities string: "ROLE_ADMIN,PRODUCT_CREATE,ORDER_VIEW,..."
     */
    private String buildAuthoritiesString(Long userId, List<Long> roleIds) {
        List<String> authorities = new ArrayList<>();
        
        for (Long roleId : roleIds) {
            roleRepository.findById(roleId).ifPresent(role -> {
                // Add role authority (ROLE_xxx)
                authorities.add("ROLE_" + role.getName());
                
                // Add all permissions for this role
                List<Long> permissionIds = rolePermissionRepository.findPermissionIdsByRoleId(roleId);
                List<PermissionEntity> permissions = permissionRepository.findAllById(permissionIds);
                permissions.forEach(p -> authorities.add(p.getName()));
            });
        }
        
        log.debug("Built {} authorities for userId={}", authorities.size(), userId);
        return String.join(",", authorities);
    }
    
    private LoginResponse generateLoginResponse(String sessionId, UserEntity userEntity, HttpServletResponse response) {
        String accessToken = jwtUtil.generateAccessToken(sessionId);
        String refreshToken = jwtUtil.generateRefreshToken(sessionId);
        
        cookieUtil.addRefreshTokenCookie(response, refreshToken);
        
        return new LoginResponse(accessToken, refreshToken, userEntity.getUsername(), userEntity.getEmail());
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
        
        Long userRoleId = roleRepository.findByName("USER")
                .map(r -> r.getId())
                .orElseThrow(() -> new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Default role not found"));
        
        userEntity = userRepository.save(userEntity);
        
        // Insert into user_roles table for many-to-many relationship
        UserRoleEntity userRoleEntity = UserRoleEntity.builder()
                .userId(userEntity.getId())
                .roleId(userRoleId)
                .build();
        userRoleRepository.save(userRoleEntity);
        
        return userEntity;
    }
    
    private void sendVerificationEmail(UserEntity userEntity) {
        try {
            UserEvent event = UserEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(UserEvent.REGISTERED)
                    .timestamp(java.time.Instant.now())
                    .source("server")
                    .userId(userEntity.getId())
                    .username(userEntity.getUsername())
                    .email(userEntity.getEmail())
                    .build();
            userKafkaProducer.send(event);
            log.info("Published UserRegisteredEvent to Kafka for userId: {}", userEntity.getId());
        } catch (Exception e) {
            log.error("Failed to publish UserRegisteredEvent to Kafka for email {}: {}", userEntity.getEmail(), e.getMessage());
            // Continue with registration even if event pushing fails
        }
    }
    
    private void validateRefreshToken(String refreshToken) {
        if (Objects.isNull(refreshToken) || refreshToken.trim().isEmpty()) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Refresh token is missing");
        }
    }
}