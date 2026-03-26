package com.example.spring_ecom.core.ratelimit;

/**
 * Các loại rate limit
 */
public enum RateLimitType {
    /**
     * Rate limit theo IP address
     */
    IP,
    
    /**
     * Rate limit theo user (cần authentication)
     */
    USER,
    
    /**
     * Rate limit theo endpoint (global)
     */
    ENDPOINT
}