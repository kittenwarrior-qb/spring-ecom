package com.example.spring_ecom.config.security;

import com.example.spring_ecom.core.exception.RateLimitExceededException;
import com.example.spring_ecom.core.ratelimit.RateLimit;
import com.example.spring_ecom.core.ratelimit.RateLimitService;
import com.example.spring_ecom.core.ratelimit.RateLimitType;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    
    private final RateLimitService rateLimitService;
    
    @Around("@annotation(rateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        
        // Lấy request info
        HttpServletRequest request = getCurrentRequest();
        String endpoint = request.getRequestURI();
        String identifier = getIdentifier(rateLimit.type(), request);
        
        // Convert duration
        Duration windowDuration = Duration.of(rateLimit.duration(), rateLimit.unit());
        
        // Check rate limit
        boolean allowed = rateLimitService.checkRateLimit(
                rateLimit.type(), 
                identifier, 
                endpoint, 
                rateLimit.limit(), 
                windowDuration
        );
        
        if (!allowed) {
            // Lấy thông tin chi tiết để throw exception
            RateLimitInfo info = rateLimitService.getRateLimitInfo(rateLimit.type(), identifier, endpoint);
            
            throw new RateLimitExceededException(
                    rateLimit.message(),
                    rateLimit.type().name(),
                    identifier,
                    endpoint,
                    Objects.nonNull(info.getCurrentRequests()) ? info.getCurrentRequests() : 0,
                    rateLimit.limit(),
                    Objects.nonNull(info.getRemainingTimeSeconds()) ? info.getRemainingTimeSeconds() : 0
            );
        }
        
        // Proceed với method execution
        return joinPoint.proceed();
    }
    
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest();
    }
    
    private String getIdentifier(RateLimitType type, HttpServletRequest request) {
        switch (type) {
            case IP:
                return getClientIpAddress(request);
            case USER:
                return getCurrentUserId();
            case ENDPOINT:
                return "global";
            default:
                return getClientIpAddress(request);
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (Objects.nonNull(xForwardedFor) && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (Objects.nonNull(xRealIp) && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            return authentication.getName(); // Có thể là user ID hoặc username
        }
        return "anonymous";
    }
}