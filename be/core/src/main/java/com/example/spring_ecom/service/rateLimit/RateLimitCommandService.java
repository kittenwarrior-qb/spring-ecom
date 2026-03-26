package com.example.spring_ecom.service.rateLimit;

import com.example.spring_ecom.core.ratelimit.RateLimitType;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitDao;
import com.example.spring_ecom.repository.redis.rateLimit.RateLimitEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Command service cho rate limit operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitCommandService {
    
    private final RateLimitDao rateLimitDao;

    public boolean checkAndIncrementRateLimit(RateLimitType rateLimitType, String identifier, String endpoint, 
                                            int maxRequests, Duration windowDuration) {
        String key = rateLimitDao.createKey(rateLimitType.name(), endpoint, identifier);
        
        return rateLimitDao.checkAndIncrement(key, maxRequests, windowDuration);
    }
    
    /**
     * Reset rate limit cho specific key
     */
    public void resetRateLimit(RateLimitType rateLimitType, String identifier, String endpoint) {
        String key = rateLimitDao.createKey(rateLimitType.name(), endpoint, identifier);
        rateLimitDao.reset(key);
        log.info("Reset rate limit for key: {}", key);
    }
    
    /**
     * Reset tất cả rate limit của identifier
     */
    public void resetAllRateLimit(String identifier) {
        rateLimitDao.deleteByIdentifier(identifier);
        log.info("Reset all rate limits for identifier: {}", identifier);
    }
    
    /**
     * Block identifier (set rate limit to 0)
     */
    public void blockIdentifier(String identifier, Duration blockDuration) {
        // Set rate limit to 0 for all endpoints
        String[] endpoints = {"login", "register", "api"};
        
        java.util.Arrays.stream(endpoints)
                .forEach(endpoint -> 
                    java.util.Arrays.stream(RateLimitType.values())
                            .forEach(type -> setCustomRateLimit(type, identifier, endpoint, 0, blockDuration))
                );
        
        log.warn("Blocked identifier: {} for duration: {}", identifier, blockDuration);
    }
    
    /**
     * Unblock identifier
     */
    public void unblockIdentifier(String identifier) {
        resetAllRateLimit(identifier);
        log.info("Unblocked identifier: {}", identifier);
    }
    
    /**
     * Increment rate limit counter manually
     */
    public void incrementRateLimit(RateLimitType rateLimitType, String identifier, String endpoint) {
        String key = rateLimitDao.createKey(rateLimitType.name(), endpoint, identifier);
        
        // Get current entity or create new one
        var existingOpt = rateLimitDao.findByKey(key);
        
        if (existingOpt.isPresent()) {
            RateLimitEntity entity = existingOpt.get();
            entity.setRequestCount(entity.getRequestCount() + 1);
            entity.setLastRequest(LocalDateTime.now());
            rateLimitDao.save(entity);
        } else {
            // Create new entity with count 1
            RateLimitEntity entity = RateLimitEntity.builder()
                    .key(key)
                    .identifier(identifier)
                    .endpoint(endpoint)
                    .rateLimitType(rateLimitType.name())
                    .requestCount(1)
                    .windowStart(LocalDateTime.now())
                    .lastRequest(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .ttl(Duration.ofMinutes(1).getSeconds()) // Default 1 minute
                    .build();
            rateLimitDao.save(entity);
        }
        
        log.debug("Incremented rate limit for key: {}", key);
    }
    
    /**
     * Set custom rate limit cho identifier
     */
    public void setCustomRateLimit(RateLimitType rateLimitType, String identifier, String endpoint, 
                           int maxRequests, Duration windowDuration) {
        String key = rateLimitDao.createKey(rateLimitType.name(), endpoint, identifier);
        
        LocalDateTime now = LocalDateTime.now();
        
        RateLimitEntity entity = RateLimitEntity.builder()
                .key(key)
                .identifier(identifier)
                .endpoint(endpoint)
                .rateLimitType(rateLimitType.name())
                .requestCount(0)
                .maxRequests(maxRequests)
                .windowStart(now)
                .lastRequest(now)
                .createdAt(now)
                .ttl(windowDuration.getSeconds())
                .build();
        
        rateLimitDao.save(entity);
        log.info("Set custom rate limit for key: {}, max: {}, window: {}", key, maxRequests, windowDuration);
    }
}