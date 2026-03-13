package com.example.spring_ecom.service.rateLimit;

import com.example.spring_ecom.repository.redis.rateLimit.RateLimitEntity;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitManagementService {
    
    private final RateLimitServiceWithFallback rateLimitService;
    
    /**
     * Lấy thông tin rate limit cho IP address
     */
    public RateLimitInfo getIpRateLimitInfo(String ipAddress, String endpoint) {
        return rateLimitService.getRateLimitInfo("IP", endpoint, ipAddress);
    }
    
    /**
     * Lấy thông tin rate limit cho user
     */
    public RateLimitInfo getUserRateLimitInfo(Long userId, String endpoint) {
        return rateLimitService.getRateLimitInfo("USER", endpoint, userId.toString());
    }
    
    /**
     * Reset rate limit cho IP
     */
    public void resetIpRateLimit(String ipAddress, String endpoint) {
        rateLimitService.resetRateLimit("IP", endpoint, ipAddress);
        log.info("Reset rate limit for IP {} on endpoint {}", ipAddress, endpoint);
    }
    
    /**
     * Reset rate limit cho user
     */
    public void resetUserRateLimit(Long userId, String endpoint) {
        rateLimitService.resetRateLimit("USER", endpoint, userId.toString());
        log.info("Reset rate limit for user {} on endpoint {}", userId, endpoint);
    }
    
    /**
     * Whitelist IP (tạm thời bỏ qua rate limit)
     */
    public void whitelistIp(String ipAddress) {
        // Implementation: có thể lưu vào database hoặc cache
        log.info("IP {} has been whitelisted", ipAddress);
    }
    
    /**
     * Blacklist IP (block hoàn toàn)
     */
    public void blacklistIp(String ipAddress) {
        // Implementation: có thể lưu vào database hoặc cache
        log.info("IP {} has been blacklisted", ipAddress);
    }
    
    /**
     * Scheduled task để cleanup expired entries
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void cleanupExpiredEntries() {
        try {
            rateLimitService.cleanupExpiredEntries();
            log.debug("Cleaned up expired rate limit entries");
        } catch (Exception e) {
            log.error("Error during rate limit cleanup: {}", e.getMessage());
        }
    }
    
    /**
     * Get rate limit statistics
     */
    public RateLimitStats getRateLimitStats() {
        // Implementation: collect statistics from Redis/memory
        return RateLimitStats.builder()
                .totalRequests(0L)
                .blockedRequests(0L)
                .activeRateLimits(0)
                .build();
    }
}