package com.example.spring_ecom.core.ratelimit;

import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitInfo;

import java.time.Duration;

public interface RateLimitService {
    
    /**
     * Check rate limit cho request
     * @param rateLimitType loại rate limit (IP, USER, ENDPOINT)
     * @param identifier định danh (IP address, user ID, etc.)
     * @param endpoint endpoint path
     * @param maxRequests số request tối đa
     * @param windowDuration thời gian window
     * @return true nếu request được phép, false nếu exceed limit
     */
    boolean checkRateLimit(RateLimitType rateLimitType, String identifier, String endpoint, 
                          int maxRequests, Duration windowDuration);
    
    /**
     * Lấy thông tin rate limit hiện tại
     */
    RateLimitInfo getRateLimitInfo(RateLimitType rateLimitType, String identifier, String endpoint);
    
    /**
     * Reset rate limit cho identifier và endpoint
     */
    void resetRateLimit(RateLimitType rateLimitType, String identifier, String endpoint);
    
    /**
     * Reset tất cả rate limit của identifier
     */
    void resetAllRateLimit(String identifier);
}