package com.example.spring_ecom.repository.redis.rateLimit.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitInfo {
    
    private String key;
    private String identifier;
    private String endpoint;
    private String rateLimitType;
    private Integer currentRequests;
    private Integer maxRequests;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Long remainingTtl; 
    private Boolean isAllowed;
    private Long retryAfter;
}