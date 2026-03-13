package com.example.spring_ecom.service.rateLimit;

import com.example.spring_ecom.repository.redis.rateLimit.RateLimitEntity;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitDao;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitServiceWithFallback {
    
    private final RateLimitDao rateLimitDao;
    
    // In-memory fallback when Redis is not available
    private final Map<String, RateLimitEntity> inMemoryCache = new ConcurrentHashMap<>();
    
    // Rate limit configurations
    private static final int LOGIN_MAX_REQUESTS = 5;
    private static final Duration LOGIN_WINDOW = Duration.ofMinutes(15);
    
    private static final int REGISTER_MAX_REQUESTS = 3;
    private static final Duration REGISTER_WINDOW = Duration.ofMinutes(30);
    
    private static final int API_MAX_REQUESTS = 100;
    private static final Duration API_WINDOW = Duration.ofMinutes(1);
    
    /**
     * Check rate limit với fallback mechanism
     */
    public boolean checkRateLimit(String rateLimitType, String endpoint, String identifier, 
                                int maxRequests, Duration windowDuration) {
        String key = createKey(rateLimitType, endpoint, identifier);
        
        try {
            // Thử Redis trước
            return rateLimitDao.checkAndIncrement(key, maxRequests, windowDuration);
        } catch (Exception e) {
            log.warn("Redis not available for rate limiting, using in-memory fallback: {}", e.getMessage());
            return checkRateLimitInMemory(key, identifier, endpoint, rateLimitType, maxRequests, windowDuration);
        }
    }
    
    /**
     * Check rate limit cho login endpoint theo IP
     */
    public boolean checkLoginRateLimit(String ipAddress) {
        return checkRateLimit("IP", "login", ipAddress, LOGIN_MAX_REQUESTS, LOGIN_WINDOW);
    }
    
    /**
     * Check rate limit cho register endpoint theo IP
     */
    public boolean checkRegisterRateLimit(String ipAddress) {
        return checkRateLimit("IP", "register", ipAddress, REGISTER_MAX_REQUESTS, REGISTER_WINDOW);
    }
    
    /**
     * Check rate limit cho API calls theo user ID
     */
    public boolean checkApiRateLimit(Long userId) {
        return checkRateLimit("USER", "api", userId.toString(), API_MAX_REQUESTS, API_WINDOW);
    }
    
    /**
     * Lấy thông tin rate limit với fallback
     */
    public RateLimitInfo getRateLimitInfo(String rateLimitType, String endpoint, String identifier) {
        String key = createKey(rateLimitType, endpoint, identifier);
        
        try {
            return rateLimitDao.getCurrentInfo(key);
        } catch (Exception e) {
            log.warn("Redis not available, getting rate limit info from in-memory cache: {}", e.getMessage());
            return getRateLimitInfoFromMemory(key);
        }
    }
    
    /**
     * Reset rate limit với fallback
     */
    public void resetRateLimit(String rateLimitType, String endpoint, String identifier) {
        String key = createKey(rateLimitType, endpoint, identifier);
        
        try {
            rateLimitDao.reset(key);
            log.info("Reset rate limit in Redis for key: {}", key);
        } catch (Exception e) {
            log.warn("Redis not available, resetting in-memory cache: {}", e.getMessage());
        }
        
        // Always remove from in-memory cache
        inMemoryCache.remove(key);
        log.info("Reset rate limit in memory for key: {}", key);
    }
    
    /**
     * In-memory rate limit check
     */
    private boolean checkRateLimitInMemory(String key, String identifier, String endpoint, 
                                         String rateLimitType, int maxRequests, Duration windowDuration) {
        LocalDateTime now = LocalDateTime.now();
        
        RateLimitEntity entity = inMemoryCache.get(key);
        
        if (entity == null) {
            // Tạo mới
            entity = RateLimitEntity.builder()
                    .key(key)
                    .identifier(identifier)
                    .endpoint(endpoint)
                    .rateLimitType(rateLimitType)
                    .requestCount(1)
                    .maxRequests(maxRequests)
                    .windowStart(now)
                    .lastRequest(now)
                    .createdAt(now)
                    .ttl(windowDuration.getSeconds())
                    .build();
            
            inMemoryCache.put(key, entity);
            return true;
        }
        
        // Check if window expired
        LocalDateTime windowEnd = entity.getWindowStart().plus(windowDuration);
        if (now.isAfter(windowEnd)) {
            // Reset window
            entity.setRequestCount(1);
            entity.setWindowStart(now);
            entity.setLastRequest(now);
            inMemoryCache.put(key, entity);
            return true;
        }
        
        // Check if exceeded limit
        if (entity.getRequestCount() >= maxRequests) {
            return false;
        }
        
        // Increment count
        entity.setRequestCount(entity.getRequestCount() + 1);
        entity.setLastRequest(now);
        inMemoryCache.put(key, entity);
        
        return true;
    }
    
    /**
     * Get rate limit info from memory
     */
    private RateLimitInfo getRateLimitInfoFromMemory(String key) {
        RateLimitEntity entity = inMemoryCache.get(key);
        
        if (entity == null) {
            return null;
        }
        
        LocalDateTime now = LocalDateTime.now();
        Duration windowDuration = Duration.ofSeconds(entity.getTtl());
        LocalDateTime windowEnd = entity.getWindowStart().plus(windowDuration);
        
        // Check if expired
        if (now.isAfter(windowEnd)) {
            inMemoryCache.remove(key);
            return null;
        }
        
        long retryAfter = Duration.between(now, windowEnd).getSeconds();
        
        return RateLimitInfo.builder()
                .key(key)
                .identifier(entity.getIdentifier())
                .endpoint(entity.getEndpoint())
                .rateLimitType(entity.getRateLimitType())
                .currentRequests(entity.getRequestCount())
                .maxRequests(entity.getMaxRequests())
                .windowStart(entity.getWindowStart())
                .windowEnd(windowEnd)
                .remainingTtl(retryAfter)
                .isAllowed(entity.getRequestCount() < entity.getMaxRequests())
                .retryAfter(entity.getRequestCount() >= entity.getMaxRequests() ? retryAfter : 0L)
                .build();
    }
    
    /**
     * Create rate limit key
     */
    private String createKey(String rateLimitType, String endpoint, String identifier) {
        return String.format("rate_limit:%s:%s:%s", rateLimitType.toLowerCase(), endpoint, identifier);
    }
    
    /**
     * Clean up expired entries from in-memory cache
     */
    public void cleanupExpiredEntries() {
        LocalDateTime now = LocalDateTime.now();
        
        inMemoryCache.entrySet().removeIf(entry -> {
            RateLimitEntity entity = entry.getValue();
            Duration windowDuration = Duration.ofSeconds(entity.getTtl());
            LocalDateTime windowEnd = entity.getWindowStart().plus(windowDuration);
            return now.isAfter(windowEnd);
        });
        
        log.debug("Cleaned up expired rate limit entries from in-memory cache");
    }
}