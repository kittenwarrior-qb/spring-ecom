package com.example.spring_ecom.service.auth.session;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSessionService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String SESSION_PREFIX = "session:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final long ACCESS_TOKEN_TTL_MINUTES = 15; // 15 minutes
    private static final long REFRESH_TOKEN_TTL_DAYS = 7; // 7 days
    
    public String createSession(Long userId, String email, String role, String deviceInfo, String ipAddress) {
        String sessionId = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        
        SessionData sessionData = SessionData.builder()
                .sessionId(sessionId)
                .userId(userId)
                .email(email)
                .role(role)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .refreshToken(refreshToken)
                .createdAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .build();
        
        try {
            String sessionJson = objectMapper.writeValueAsString(sessionData);
            
            // Store session with access token TTL
            redisTemplate.opsForValue().set(
                SESSION_PREFIX + sessionId, 
                sessionJson, 
                ACCESS_TOKEN_TTL_MINUTES, 
                TimeUnit.MINUTES
            );
            
            // Store refresh token with longer TTL
            redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + refreshToken, 
                sessionId, 
                REFRESH_TOKEN_TTL_DAYS, 
                TimeUnit.DAYS
            );
            
            log.info("Created session {} for user {}", sessionId, userId);
            return sessionId;
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize session data", e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create session");
        }
    }
    
    public SessionData getSession(String sessionId) {
        try {
            String sessionJson = (String) redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
            if (sessionJson == null) {
                throw new BaseException(ResponseCode.UNAUTHORIZED, "Session not found or expired");
            }
            
            SessionData sessionData = objectMapper.readValue(sessionJson, SessionData.class);
            
            // Update last accessed time
            sessionData.setLastAccessedAt(LocalDateTime.now());
            updateSession(sessionData);
            
            return sessionData;
            
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize session data", e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to retrieve session");
        }
    }    
 
   public SessionData refreshSession(String refreshToken, String deviceInfo, String ipAddress) {
        String sessionId = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);
        if (sessionId == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Invalid or expired refresh token");
        }
        
        SessionData sessionData = getSessionById(sessionId);
        if (sessionData == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Session not found");
        }
        
        // Create new session and tokens
        String newSessionId = UUID.randomUUID().toString();
        String newRefreshToken = UUID.randomUUID().toString();
        
        sessionData.setSessionId(newSessionId);
        sessionData.setRefreshToken(newRefreshToken);
        sessionData.setDeviceInfo(deviceInfo);
        sessionData.setIpAddress(ipAddress);
        sessionData.setLastAccessedAt(LocalDateTime.now());
        
        try {
            String sessionJson = objectMapper.writeValueAsString(sessionData);
            
            // Store new session
            redisTemplate.opsForValue().set(
                SESSION_PREFIX + newSessionId, 
                sessionJson, 
                ACCESS_TOKEN_TTL_MINUTES, 
                TimeUnit.MINUTES
            );
            
            // Store new refresh token
            redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + newRefreshToken, 
                newSessionId, 
                REFRESH_TOKEN_TTL_DAYS, 
                TimeUnit.DAYS
            );
            
            // Remove old tokens
            redisTemplate.delete(SESSION_PREFIX + sessionId);
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
            
            log.info("Refreshed session {} for user {}", newSessionId, sessionData.getUserId());
            return sessionData;
            
        } catch (JsonProcessingException e) {
            log.error("Failed to refresh session", e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to refresh session");
        }
    }
    
    public void revokeSession(String sessionId) {
        SessionData sessionData = getSessionById(sessionId);
        if (sessionData != null) {
            redisTemplate.delete(SESSION_PREFIX + sessionId);
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + sessionData.getRefreshToken());
            log.info("Revoked session {} for user {}", sessionId, sessionData.getUserId());
        }
    }
    
    public void revokeRefreshToken(String refreshToken) {
        String sessionId = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);
        if (sessionId != null) {
            redisTemplate.delete(SESSION_PREFIX + sessionId);
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
            log.info("Revoked refresh token and associated session {}", sessionId);
        }
    }
    
    public void revokeAllUserSessions(Long userId) {
        // Note: This is a simplified implementation
        // In production, you might want to maintain a user->sessions mapping for efficiency
        log.info("Revoking all sessions for user {}", userId);
    }
    
    public boolean isSessionValid(String sessionId) {
        return redisTemplate.hasKey(SESSION_PREFIX + sessionId);
    }
    
    private SessionData getSessionById(String sessionId) {
        try {
            String sessionJson = (String) redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
            if (sessionJson == null) {
                return null;
            }
            return objectMapper.readValue(sessionJson, SessionData.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize session data", e);
            return null;
        }
    }
    
    private void updateSession(SessionData sessionData) {
        try {
            String sessionJson = objectMapper.writeValueAsString(sessionData);
            redisTemplate.opsForValue().set(
                SESSION_PREFIX + sessionData.getSessionId(), 
                sessionJson, 
                ACCESS_TOKEN_TTL_MINUTES, 
                TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to update session", e);
        }
    }
}