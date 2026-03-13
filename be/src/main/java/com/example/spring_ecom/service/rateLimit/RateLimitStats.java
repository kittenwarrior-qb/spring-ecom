package com.example.spring_ecom.service.rateLimit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitStats {
    
    private Long totalRequests;
    private Long blockedRequests;
    private Integer activeRateLimits;
    private Double blockRate;
    
    public Double getBlockRate() {
        if (totalRequests == null || totalRequests == 0) {
            return 0.0;
        }
        return (blockedRequests != null ? blockedRequests.doubleValue() : 0.0) / totalRequests.doubleValue() * 100;
    }
}