package com.example.spring_ecom.service.rateLimit;

import com.example.spring_ecom.core.ratelimit.RateLimitType;
import com.example.spring_ecom.domain.rateLimit.RateLimitResult;
import com.example.spring_ecom.domain.rateLimit.RateLimitStats;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Use case service implementation cho rate limit business logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitUseCaseService implements RateLimitUseCase {
    
    private final RateLimitQueryService rateLimitQueryService;
    private final RateLimitCommandService rateLimitCommandService;
    
    // Rate limit configurations
    private static final int LOGIN_MAX_REQUESTS = 5;
    private static final Duration LOGIN_WINDOW = Duration.ofMinutes(15);
    
    private static final int REGISTER_MAX_REQUESTS = 3;
    private static final Duration REGISTER_WINDOW = Duration.ofMinutes(30);
    
    private static final int API_MAX_REQUESTS = 100;
    private static final Duration API_WINDOW = Duration.ofMinutes(1);
    
    private static final Duration SUSPICIOUS_BLOCK_DURATION = Duration.ofHours(24);
    
    @Override
    public RateLimitResult checkLoginRateLimit(String ipAddress) {
        log.debug("Checking login rate limit for IP: {}", ipAddress);
        
        boolean allowed = rateLimitCommandService.checkAndIncrementRateLimit(
                RateLimitType.IP, ipAddress, "login", LOGIN_MAX_REQUESTS, LOGIN_WINDOW);
        
        RateLimitInfo info = rateLimitQueryService.getRateLimitInfo(
                RateLimitType.IP.name(), ipAddress, "login");
        
        if (!allowed) {
            log.warn("Login rate limit exceeded for IP: {}", ipAddress);
        }
        
        return RateLimitResult.builder()
                .isAllowed(allowed)
                .rateLimitInfo(info)
                .build();
    }
    
    @Override
    public RateLimitResult checkRegisterRateLimit(String ipAddress) {
        log.debug("Checking register rate limit for IP: {}", ipAddress);
        
        boolean allowed = rateLimitCommandService.checkAndIncrementRateLimit(
                RateLimitType.IP, ipAddress, "register", REGISTER_MAX_REQUESTS, REGISTER_WINDOW);
        
        RateLimitInfo info = rateLimitQueryService.getRateLimitInfo(
                RateLimitType.IP.name(), ipAddress, "register");
        
        if (!allowed) {
            log.warn("Register rate limit exceeded for IP: {}", ipAddress);
        }
        
        return RateLimitResult.builder()
                .isAllowed(allowed)
                .rateLimitInfo(info)
                .build();
    }
    
    @Override
    public RateLimitResult checkApiRateLimit(Long userId) {
        log.debug("Checking API rate limit for user: {}", userId);
        
        String userIdStr = userId.toString();
        boolean allowed = rateLimitCommandService.checkAndIncrementRateLimit(
                RateLimitType.USER, userIdStr, "api", API_MAX_REQUESTS, API_WINDOW);
        
        RateLimitInfo info = rateLimitQueryService.getRateLimitInfo(
                RateLimitType.USER.name(), userIdStr, "api");
        
        if (!allowed) {
            log.warn("API rate limit exceeded for user: {}", userId);
        }
        
        return RateLimitResult.builder()
                .isAllowed(allowed)
                .rateLimitInfo(info)
                .build();
    }
    
    @Override
    public RateLimitResult checkCustomRateLimit(RateLimitType type, String identifier, String endpoint, 
                                               int maxRequests, Duration windowDuration) {
        log.debug("Checking custom rate limit - Type: {}, Identifier: {}, Endpoint: {}", 
                type, identifier, endpoint);
        
        boolean allowed = rateLimitCommandService.checkAndIncrementRateLimit(
                type, identifier, endpoint, maxRequests, windowDuration);
        
        RateLimitInfo info = rateLimitQueryService.getRateLimitInfo(
                type.name(), identifier, endpoint);
        
        return RateLimitResult.builder()
                .isAllowed(allowed)
                .rateLimitInfo(info)
                .build();
    }
    
    @Override
    public RateLimitInfo getRateLimitInfo(RateLimitType type, String identifier, String endpoint) {
        return rateLimitQueryService.getRateLimitInfo(type.name(), identifier, endpoint);
    }
    
    @Override
    public void resetRateLimit(RateLimitType type, String identifier, String endpoint) {
        log.info("Resetting rate limit - Type: {}, Identifier: {}, Endpoint: {}", 
                type, identifier, endpoint);
        rateLimitCommandService.resetRateLimit(type, identifier, endpoint);
    }
    
    @Override
    public void blockSuspiciousActivity(String identifier, String reason) {
        log.warn("Blocking suspicious activity - Identifier: {}, Reason: {}", identifier, reason);
        rateLimitCommandService.blockIdentifier(identifier, SUSPICIOUS_BLOCK_DURATION);
    }
    
    @Override
    public void unblockIdentifier(String identifier, String reason) {
        log.info("Unblocking identifier: {}, Reason: {}", identifier, reason);
        rateLimitCommandService.unblockIdentifier(identifier);
    }
    
    @Override
    public RateLimitStats getRateLimitStatistics() {
        return rateLimitQueryService.getRateLimitStats();
    }
    
    @Override
    public void cleanupExpiredEntries() {
        log.debug("Cleaning up expired rate limit entries");
        // Implementation will be in command service
    }
}