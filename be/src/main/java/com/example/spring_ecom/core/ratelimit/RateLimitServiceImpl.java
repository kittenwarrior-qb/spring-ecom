package com.example.spring_ecom.core.ratelimit;

import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitDao;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {
    
    private final RateLimitDao rateLimitDao;
    
    @Override
    public boolean checkRateLimit(RateLimitType rateLimitType, String identifier, String endpoint, 
                                 int maxRequests, Duration windowDuration) {
        String key = rateLimitDao.createKey(rateLimitType.name(), endpoint, identifier);
        
        boolean allowed = rateLimitDao.checkAndIncrement(key, maxRequests, windowDuration);
        
        if (!allowed) {
            log.warn("Rate limit exceeded - Type: {}, Identifier: {}, Endpoint: {}, Max: {}", 
                    rateLimitType, identifier, endpoint, maxRequests);
        }
        
        return allowed;
    }
    
    @Override
    public RateLimitInfo getRateLimitInfo(RateLimitType rateLimitType, String identifier, String endpoint) {
        String key = rateLimitDao.createKey(rateLimitType.name(), endpoint, identifier);
        return rateLimitDao.getCurrentInfo(key);
    }
    
    @Override
    public void resetRateLimit(RateLimitType rateLimitType, String identifier, String endpoint) {
        String key = rateLimitDao.createKey(rateLimitType.name(), endpoint, identifier);
        rateLimitDao.deleteByKey(key);
        log.info("Reset rate limit - Type: {}, Identifier: {}, Endpoint: {}", 
                rateLimitType, identifier, endpoint);
    }
    
    @Override
    public void resetAllRateLimit(String identifier) {
        rateLimitDao.deleteByIdentifier(identifier);
        log.info("Reset all rate limits for identifier: {}", identifier);
    }
}