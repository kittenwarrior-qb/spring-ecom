package com.example.spring_ecom.service.auth.redis;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.repository.redis.session.RedisEntity;
import com.example.spring_ecom.repository.redis.session.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceWithFallback {
    
    private final RedisRepository redisRepository;
    
    // In-memory fallback when Redis is not available
    private final Map<String, RedisEntity> inMemoryCache = new ConcurrentHashMap<>();
    
    private static final long ACCESS_TOKEN_TTL_SECONDS = 900; // 15 minutes
    
    public String createSession(Long userId, String username, String email, String role, String firstName, String lastName, 
                               String phoneNumber, String address, String city, String district, String ward,
                               String deviceInfo, String ipAddress) {
        String sessionId = UUID.randomUUID().toString();
        
        RedisEntity session = RedisEntity.builder()
                .sessionId(sessionId)
                .userId(userId)
                .username(username)
                .email(email)
                .role(role)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .address(address)
                .city(city)
                .district(district)
                .ward(ward)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .ttl(ACCESS_TOKEN_TTL_SECONDS)
                .build();
        
        try {
            redisRepository.save(session);
            log.info("Created session {} for user {} in Redis", sessionId, userId);
        } catch (Exception e) {
            log.warn("Redis not available, using in-memory fallback for session {}: {}", sessionId, e.getMessage());
            inMemoryCache.put(sessionId, session);
        }
        
        return sessionId;
    }
    
    // Overloaded method for backward compatibility
    public String createSession(Long userId, String email, String role, String deviceInfo, String ipAddress) {
        return createSession(userId, null, email, role, null, null, null, null, null, null, null, deviceInfo, ipAddress);
    }
    
    public Optional<RedisEntity> getSession(String sessionId) {
        try {
            Optional<RedisEntity> session = redisRepository.findById(sessionId);
            
            if (session.isPresent()) {
                // Update last accessed time
                RedisEntity sessionEntity = session.get();
                sessionEntity.setLastAccessedAt(LocalDateTime.now());
                redisRepository.save(sessionEntity);
                return session;
            }
        } catch (Exception e) {
            log.warn("Redis not available, checking in-memory fallback: {}", e.getMessage());
        }
        
        // Fallback to in-memory cache
        RedisEntity session = inMemoryCache.get(sessionId);
        if (session != null) {
            // Check if session is expired (simple TTL check)
            LocalDateTime expiryTime = session.getCreatedAt().plusSeconds(ACCESS_TOKEN_TTL_SECONDS);
            if (LocalDateTime.now().isAfter(expiryTime)) {
                inMemoryCache.remove(sessionId);
                return Optional.empty();
            }
            
            // Update last accessed time
            session.setLastAccessedAt(LocalDateTime.now());
            inMemoryCache.put(sessionId, session);
            return Optional.of(session);
        }
        
        return Optional.empty();
    }
    
    public RedisEntity validateSession(String sessionId) {
        return getSession(sessionId)
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED, "Session not found or expired"));
    }
    
    public void revokeSession(String sessionId) {
        try {
            redisRepository.deleteById(sessionId);
            log.info("Revoked session {} from Redis", sessionId);
        } catch (Exception e) {
            log.warn("Redis not available, removing from in-memory fallback: {}", e.getMessage());
        }
        
        // Also remove from in-memory cache
        inMemoryCache.remove(sessionId);
        log.info("Revoked session {} from in-memory cache", sessionId);
    }
    
    public void revokeAllUserSessions(Long userId) {
        try {
            redisRepository.deleteByUserId(userId);
            log.info("Revoked all sessions for user {} from Redis", userId);
        } catch (Exception e) {
            log.warn("Redis not available for revoking user sessions: {}", e.getMessage());
        }
        
        // Remove from in-memory cache
        inMemoryCache.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));
        log.info("Revoked all sessions for user {} from in-memory cache", userId);
    }
    
    public boolean isSessionValid(String sessionId) {
        try {
            if (redisRepository.existsById(sessionId)) {
                return true;
            }
        } catch (Exception e) {
            log.warn("Redis not available, checking in-memory fallback: {}", e.getMessage());
        }
        
        // Check in-memory cache
        RedisEntity session = inMemoryCache.get(sessionId);
        if (session != null) {
            LocalDateTime expiryTime = session.getCreatedAt().plusSeconds(ACCESS_TOKEN_TTL_SECONDS);
            if (LocalDateTime.now().isAfter(expiryTime)) {
                inMemoryCache.remove(sessionId);
                return false;
            }
            return true;
        }
        
        return false;
    }
}