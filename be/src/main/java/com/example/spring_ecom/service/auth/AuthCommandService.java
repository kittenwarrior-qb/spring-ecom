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
import com.example.spring_ecom.repository.database.auth.RefreshTokenEntity;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserRepository;
import com.example.spring_ecom.service.auth.refreshToken.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;

    public AuthResponse login(LoginDto command, String deviceInfo, String ipAddress, HttpServletResponse response) {
        UserEntity userEntity = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED, "Wrong email"));
        
        if (!userEntity.getIsActive()) {
            throw new BaseException(ResponseCode.FORBIDDEN, "Account is not activated");
        }

        if (!passwordEncoder.matches(command.password(), userEntity.getPassword())) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Wrong password");
        }

        userEntity.setLastLoginAt(LocalDateTime.now());
        userRepository.save(userEntity);

        String accessToken = jwtUtil.generateToken(
                userEntity.getEmail(),
                userEntity.getId(),
                userEntity.getRole().name()
        );
        
        String refreshToken = refreshTokenService.createRefreshToken(userEntity.getId(), deviceInfo, ipAddress);
        cookieUtil.addRefreshTokenCookie(response, refreshToken);

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
        userEntity.setRole(UserRole.USER);
        userEntity.setIsActive(true);
        userEntity.setIsEmailVerified(false);

        userEntity = userRepository.save(userEntity);

        String accessToken = jwtUtil.generateToken(
                userEntity.getEmail(),
                userEntity.getId(),
                userEntity.getRole().name()
        );
        
        String refreshToken = refreshTokenService.createRefreshToken(userEntity.getId(), deviceInfo, ipAddress);
        cookieUtil.addRefreshTokenCookie(response, refreshToken);

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
    
    public AuthResponse refreshToken(String oldRefreshToken, String deviceInfo, String ipAddress, HttpServletResponse response) {
        if (oldRefreshToken == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Refresh token is missing");
        }
        
        RefreshTokenEntity refreshToken = refreshTokenService.validateRefreshToken(oldRefreshToken);
        
        UserEntity userEntity = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        
        String accessToken = jwtUtil.generateToken(
                userEntity.getEmail(),
                userEntity.getId(),
                userEntity.getRole().name()
        );
        
        String newRefreshToken = refreshTokenService.createRefreshToken(userEntity.getId(), deviceInfo, ipAddress);
        refreshTokenService.revokeRefreshToken(oldRefreshToken, newRefreshToken);
        cookieUtil.addRefreshTokenCookie(response, newRefreshToken);
        
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
    
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            refreshTokenService.revokeRefreshToken(refreshToken, null);
        }
    }
}
