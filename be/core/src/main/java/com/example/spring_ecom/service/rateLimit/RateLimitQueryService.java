package com.example.spring_ecom.service.rateLimit;

import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitDao;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitInfo;
import com.example.spring_ecom.repository.redis.rateLimit.RateLimitEntity;
import com.example.spring_ecom.domain.rateLimit.RateLimitStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Query service cho rate limit operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitQueryService {
    
    private final RateLimitDao rateLimitDao;
    
    /**
     * Lấy thông tin rate limit hiện tại
     */
    public RateLimitInfo getRateLimitInfo(String rateLimitType, String identifier, String endpoint) {
        String key = rateLimitDao.createKey(rateLimitType, endpoint, identifier);
        return rateLimitDao.getCurrentInfo(key);
    }
    
    /**
     * Lấy tất cả rate limit của một identifier
     */
    public List<RateLimitInfo> getAllRateLimitsByIdentifier(String identifier) {
        List<RateLimitEntity> entities = rateLimitDao.findByIdentifier(identifier);
        return entities.stream()
                .map(this::convertToInfo)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy rate limit stats
     */
    public RateLimitStats getRateLimitStats() {
        return RateLimitStats.builder()
                .totalRequests(0L)
                .blockedRequests(0L)
                .activeRateLimits(0)
                .build();
    }
    
    /**
     * Check xem có bị rate limit không (không increment counter)
     */
    public boolean isRateLimited(String rateLimitType, String identifier, String endpoint, 
                         int maxRequests, Duration windowDuration) {
        String key = rateLimitDao.createKey(rateLimitType, endpoint, identifier);
        RateLimitInfo info = rateLimitDao.getCurrentInfo(key);
        
        if (Objects.isNull(info)) {
            return false; // No rate limit entry = not limited
        }
        
        // Check if window expired
        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(info.getWindowEnd()) && now.isAfter(info.getWindowEnd())) {
            return false; // Window expired = not limited
        }
        
        // Check if exceeded limit
        return Objects.nonNull(info.getCurrentRequests()) && 
               info.getCurrentRequests() >= maxRequests;
    }
    
    /**
     * Lấy remaining requests
     */
    public int getRemainingRequests(String rateLimitType, String identifier, String endpoint, int maxRequests) {
        RateLimitInfo info = getRateLimitInfo(rateLimitType, identifier, endpoint);
        
        if (Objects.isNull(info) || Objects.isNull(info.getCurrentRequests())) {
            return maxRequests;
        }
        
        return Math.max(0, maxRequests - info.getCurrentRequests());
    }
    
    /**
     * Lấy thời gian reset window
     */
    public long getResetTimeSeconds(String rateLimitType, String identifier, String endpoint) {
        RateLimitInfo info = getRateLimitInfo(rateLimitType, identifier, endpoint);
        
        if (Objects.isNull(info) || Objects.isNull(info.getRemainingTimeSeconds())) {
            return 0;
        }
        
        return Math.max(0, info.getRemainingTimeSeconds());
    }
    
    private RateLimitInfo convertToInfo(RateLimitEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        Duration windowDuration = Duration.ofSeconds(entity.getTtl());
        LocalDateTime windowEnd = entity.getWindowStart().plus(windowDuration);
        
        return RateLimitInfo.builder()
                .key(entity.getKey())
                .identifier(entity.getIdentifier())
                .endpoint(entity.getEndpoint())
                .rateLimitType(entity.getRateLimitType())
                .currentRequests(entity.getRequestCount())
                .maxRequests(entity.getMaxRequests())
                .windowStart(entity.getWindowStart())
                .windowEnd(windowEnd)
                .remainingTimeSeconds(Duration.between(now, windowEnd).getSeconds())
                .isAllowed(entity.getRequestCount() < entity.getMaxRequests())
                .build();
    }
}