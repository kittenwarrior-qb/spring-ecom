package com.example.spring_ecom.domain.rateLimit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitStats {
    
    private Long totalRequests;
    private Long blockedRequests;
    private Integer activeRateLimits;
    
    public Double getBlockRate() {
        if (Objects.isNull(totalRequests) || totalRequests == 0) {
            return 0.0;
        }
        return (Objects.nonNull(blockedRequests) ? blockedRequests.doubleValue() : 0.0) / totalRequests.doubleValue() * 100;
    }
}