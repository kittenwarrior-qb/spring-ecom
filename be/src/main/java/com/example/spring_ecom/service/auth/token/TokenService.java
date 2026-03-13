package com.example.spring_ecom.service.auth.token;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.JwtUtil;
import com.example.spring_ecom.repository.redis.session.RedisEntity;
import com.example.spring_ecom.service.auth.redis.RedisServiceWithFallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    
    private final JwtUtil jwtUtil;
    private final RedisServiceWithFallback redisService;
    
    public TokenInfo validateAccessToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Invalid or expired access token");
        }
        
        String tokenType = jwtUtil.extractTokenType(token);
        if (!"access".equals(tokenType)) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Invalid token type");
        }
        
        String sessionId = jwtUtil.extractSessionId(token);
        
        try {
            RedisEntity session = redisService.validateSession(sessionId);
            
            return TokenInfo.builder()
                    .sessionId(sessionId)
                    .userId(session.getUserId())
                    .email(session.getEmail())
                    .role(session.getRole())
                    .build();
        } catch (BaseException e) {
            log.warn("Access token validation failed - Session validation error for sessionId: {}, error: {}", 
                    sessionId, e.getMessage());
            throw e;
        }
    }
    
    public TokenInfo validateRefreshToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Invalid or expired refresh token");
        }
        
        String tokenType = jwtUtil.extractTokenType(token);
        if (!"refresh".equals(tokenType)) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "Invalid token type");
        }
        
        String sessionId = jwtUtil.extractSessionId(token);
        
        try {
            RedisEntity session = redisService.validateSession(sessionId);
            
            return TokenInfo.builder()
                    .sessionId(sessionId)
                    .userId(session.getUserId())
                    .email(session.getEmail())
                    .role(session.getRole())
                    .build();
        } catch (BaseException e) {
            log.warn("Refresh token validation failed - Session validation error for sessionId: {}, error: {}", 
                    sessionId, e.getMessage());
            throw e;
        }
    }
    
    public String extractSessionId(String token) {
        return jwtUtil.extractSessionId(token);
    }
}