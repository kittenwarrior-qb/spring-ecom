package com.example.spring_ecom.service.rateLimit;

import com.example.spring_ecom.core.ratelimit.RateLimitType;
import com.example.spring_ecom.domain.rateLimit.RateLimitResult;
import com.example.spring_ecom.domain.rateLimit.RateLimitStats;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitInfo;

import java.time.Duration;
import java.util.List;

/**
 * Use case interface cho rate limit business logic
 */
public interface RateLimitUseCase {
    
    /**
     * Check rate limit cho login endpoint
     */
    RateLimitResult checkLoginRateLimit(String ipAddress);
    
    /**
     * Check rate limit cho register endpoint
     */
    RateLimitResult checkRegisterRateLimit(String ipAddress);
    
    /**
     * Check rate limit cho API calls
     */
    RateLimitResult checkApiRateLimit(Long userId);
    
    /**
     * Check rate limit với custom config
     */
    RateLimitResult checkCustomRateLimit(RateLimitType type, String identifier, String endpoint, 
                                        int maxRequests, Duration windowDuration);
    
    /**
     * Lấy rate limit info với business logic
     */
    RateLimitInfo getRateLimitInfo(RateLimitType type, String identifier, String endpoint);
    
    /**
     * Reset rate limit với validation
     */
    void resetRateLimit(RateLimitType type, String identifier, String endpoint);
    
    /**
     * Block suspicious IP/User
     */
    void blockSuspiciousActivity(String identifier, String reason);
    
    /**
     * Unblock IP/User
     */
    void unblockIdentifier(String identifier, String reason);
    
    /**
     * Lấy rate limit statistics
     */
    RateLimitStats getRateLimitStatistics();
    
    /**
     * Cleanup expired rate limit entries
     */
    void cleanupExpiredEntries();
}