package com.example.spring_ecom.domain.rateLimit;

import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitResult {
    
    private Boolean isAllowed;
    private RateLimitInfo rateLimitInfo;
    
    public boolean isBlocked() {
        return !isAllowed;
    }
    
    public int getRemainingRequests() {
        if (Objects.isNull(rateLimitInfo)) return 0;
        return Math.max(0, rateLimitInfo.getMaxRequests() - rateLimitInfo.getCurrentRequests());
    }
    
    public Long getRetryAfter() {
        return Objects.nonNull(rateLimitInfo) ? rateLimitInfo.getRemainingTimeSeconds() : null;
    }
}