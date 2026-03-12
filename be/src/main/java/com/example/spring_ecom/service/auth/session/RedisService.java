package com.example.spring_ecom.service.auth.session;

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
        redisRepository.deleteById(sessionId);
        log.info("Revoked session {}", sessionId);
    }
    
    public void revokeAllUserSessions(Long userId) {
        redisRepository.deleteByUserId(userId);
        log.info("Revoked all sessions for user {}", userId);
    }
    
    public boolean isSessionValid(String sessionId) {
        return redisRepository.existsById(sessionId);
    }
}