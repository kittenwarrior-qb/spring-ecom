package com.example.spring_ecom.service.auth.refreshToken;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.repository.database.auth.RefreshTokenEntity;
import com.example.spring_ecom.repository.database.auth.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private static final int REFRESH_TOKEN_EXPIRY_DAYS = 7;
    
    public String createRefreshToken(Long userId, String deviceInfo, String ipAddress) {
        String token = UUID.randomUUID().toString();
        
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .userId(userId)
                .token(token)
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS))
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .build();
        
        refreshTokenRepository.save(refreshToken);
        return token;
    }
    
    public RefreshTokenEntity validateRefreshToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED, "Invalid refresh token"));
        if (refreshToken.getRevokedAt() != null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Refresh token has been revoked");
        }
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Refresh token has expired");
        }
        return refreshToken;
    }
    
    public void revokeRefreshToken(String token, String replacedByToken) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED, "Invalid refresh token"));
        
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshToken.setReplacedByToken(replacedByToken);
        refreshTokenRepository.save(refreshToken);
    }
    
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
    
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBeforeOrRevokedAtIsNotNull(LocalDateTime.now());
    }
}
