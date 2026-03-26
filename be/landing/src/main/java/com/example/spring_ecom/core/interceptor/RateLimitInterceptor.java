package com.example.spring_ecom.core.interceptor;

import com.example.spring_ecom.core.ratelimit.RateLimit;
import com.example.spring_ecom.core.ratelimit.RateLimitType;
import com.example.spring_ecom.core.ratelimit.RateLimitService;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.HttpUtil;
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
    
    private final RateLimitService rateLimitService;
    
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
        
        String endpoint = handlerMethod.getMethod().getName(); // Sử dụng method name làm endpoint
        String identifier = getIdentifier(request, rateLimit.type());
        
        Duration windowDuration = Duration.of(rateLimit.duration(), rateLimit.unit());
        
        boolean isAllowed = rateLimitService.checkRateLimit(
                rateLimit.type(), identifier, endpoint, 
                rateLimit.limit(), windowDuration
        );
        
        if (!isAllowed) {
            log.warn("Rate limit exceeded for {} {} on endpoint {}", 
                    rateLimit.type(), identifier, endpoint);
            
            // Set rate limit headers
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.limit()));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + windowDuration.toMillis()));
            
            throw new BaseException(ResponseCode.TOO_MANY_REQUESTS, rateLimit.message());
        }
        
        return true;
    }
    
    private String getIdentifier(HttpServletRequest request, RateLimitType rateLimitType) {
        switch (rateLimitType) {
            case USER:
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    return auth.getName(); 
                }
                // Fallback to IP if user not authenticated
                return HttpUtil.getClientIpAddress(request);
                
            case IP:
            case ENDPOINT:
            default:
                return HttpUtil.getClientIpAddress(request);
        }
    }
}