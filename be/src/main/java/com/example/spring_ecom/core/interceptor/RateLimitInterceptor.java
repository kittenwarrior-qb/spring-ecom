package com.example.spring_ecom.core.interceptor;

import com.example.spring_ecom.core.annotation.RateLimit;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.HttpUtil;
import com.example.spring_ecom.service.rateLimit.RateLimitServiceWithFallback;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final RateLimitServiceWithFallback rateLimitService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        
        if (rateLimit == null) {
            return true;
        }
        
        String rateLimitType = rateLimit.type();
        String endpoint = rateLimit.endpoint().isEmpty() ? 
                handlerMethod.getMethod().getName() : rateLimit.endpoint();
        String identifier = getIdentifier(request, rateLimitType);
        
        Duration windowDuration = Duration.ofMinutes(rateLimit.windowMinutes());
        
        boolean isAllowed = rateLimitService.checkRateLimit(
                rateLimitType, endpoint, identifier, 
                rateLimit.maxRequests(), windowDuration
        );
        
        if (!isAllowed) {
            log.warn("Rate limit exceeded for {} {} on endpoint {}", 
                    rateLimitType, identifier, endpoint);
            
            // Set rate limit headers
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.maxRequests()));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + windowDuration.toMillis()));
            
            throw new BaseException(ResponseCode.TOO_MANY_REQUESTS, rateLimit.message());
        }
        
        return true;
    }
    
    private String getIdentifier(HttpServletRequest request, String rateLimitType) {
        switch (rateLimitType.toUpperCase()) {
            case "USER":
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    return auth.getName(); // username hoặc user ID
                }
                // Fallback to IP if user not authenticated
                return HttpUtil.getClientIpAddress(request);
                
            case "IP":
            default:
                return HttpUtil.getClientIpAddress(request);
        }
    }
}