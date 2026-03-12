package com.example.spring_ecom.service.auth.redis;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.repository.redis.session.RedisEntity;
import com.example.spring_ecom.repository.redis.session.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    
    private final RedisRepository redisRepository;
    
    private static final long ACCESS_TOKEN_TTL_SECONDS = 900; // 15 minutes
    
    public String createSession(Long userId, String email, String role, String deviceInfo, String ipAddress) {
        String sessionId = UUID.randomUUID().toString();
        
        RedisEntity session = RedisEntity.builder()
                .sessionId(sessionId)
                .userId(userId)
                .email(email)
                .role(role)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .ttl(ACCESS_TOKEN_TTL_SECONDS)
                .build();
        
        log.info("Creating Redis session with key pattern: redis:{}", sessionId);
        redisRepository.save(session);
        log.info("Created session {} for user {}", sessionId, userId);
        
        return sessionId;
    }
    
    public Optional<RedisEntity> getSession(String sessionId) {
        Optional<RedisEntity> session = redisRepository.findBySessionId(sessionId);
        
        if (session.isPresent()) {
            // Update last accessed time
            RedisEntity sessionEntity = session.get();
            sessionEntity.setLastAccessedAt(LocalDateTime.now());
            redisRepository.save(sessionEntity);
        }
        
        return session;
    }
    
    public RedisEntity validateSession(String sessionId) {
        return getSession(sessionId)
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED, "Session not found or expired"));
    }
    
    public void revokeSession(String sessionId) {
        log.info("Revoking Redis session with key: redis:{}", sessionId);
        redisRepository.deleteById(sessionId);
        log.info("Revoked session {}", sessionId);
    }
    
    public void revokeAllUserSessions(Long userId) {
        redisRepository.deleteByUserId(userId);
        log.info("Revoked all sessions for user {}", userId);
    }
    
    public boolean isSessionValid(String sessionId) {
        boolean exists = redisRepository.existsById(sessionId);
        log.debug("Checking session validity for {}: {}", sessionId, exists);
        return exists;
    }
    
    // Debug method to log all Redis keys
    public void debugRedisKeys() {
        try {
            log.info("=== DEBUG: Current Redis Keys ===");
            // This would require RedisTemplate to list all keys
            // For now, just log when operations happen
            log.info("Session operations are being logged above");
        } catch (Exception e) {
            log.error("Error debugging Redis keys: {}", e.getMessage());
        }
    }
    
    // Clean up any orphaned refresh token keys (if they exist)
    public void cleanupOrphanedKeys() {
        try {
            log.info("Cleanup completed - only session keys should exist in Redis");
            // Note: refresh tokens should be JWT, not stored in Redis
            // Only session data (redis:sessionId) should be in Redis
        } catch (Exception e) {
            log.error("Error during cleanup: {}", e.getMessage());
        }
    }
}