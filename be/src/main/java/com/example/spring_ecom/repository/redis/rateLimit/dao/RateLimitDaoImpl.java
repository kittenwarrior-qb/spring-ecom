package com.example.spring_ecom.repository.redis.rateLimit.dao;

import com.example.spring_ecom.repository.redis.rateLimit.RateLimitEntity;
import com.example.spring_ecom.repository.redis.rateLimit.RateLimitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitDaoImpl implements RateLimitDao {
    
    private final RateLimitRepository rateLimitRepository;
    
    @Override
    public RateLimitEntity save(RateLimitEntity entity) {
        return rateLimitRepository.save(entity);
    }
    
    @Override
    public Optional<RateLimitEntity> findByKey(String key) {
        return rateLimitRepository.findById(key);
    }
    
    @Override
    public String createKey(String rateLimitType, String endpoint, String identifier) {
        return String.format("rate_limit:%s:%s:%s", rateLimitType.toLowerCase(), endpoint, identifier);
    }
    
    @Override
    public boolean checkAndIncrement(String key, int maxRequests, Duration windowDuration) {
        Optional<RateLimitEntity> existingOpt = findByKey(key);
        LocalDateTime now = LocalDateTime.now();
        
        if (existingOpt.isEmpty()) {
            // Tạo mới rate limit entry
            RateLimitEntity newEntity = RateLimitEntity.builder()
                    .key(key)
                    .requestCount(1)
                    .maxRequests(maxRequests)
                    .windowStart(now)
                    .lastRequest(now)
                    .createdAt(now)
                    .ttl(windowDuration.getSeconds())
                    .build();
            
            save(newEntity);
            log.debug("Created new rate limit entry for key: {}", key);
            return true;
        }
        
        RateLimitEntity existing = existingOpt.get();
        
        // Check nếu window đã hết hạn
        if (now.isAfter(existing.getWindowStart().plus(windowDuration))) {
            // Reset window
            existing.setRequestCount(1);
            existing.setWindowStart(now);
            existing.setLastRequest(now);
            existing.setTtl(windowDuration.getSeconds());
            
            save(existing);
            log.debug("Reset rate limit window for key: {}", key);
            return true;
        }
        
        // Check nếu đã exceed limit
        if (existing.getRequestCount() >= maxRequests) {
            log.warn("Rate limit exceeded for key: {}, current: {}, max: {}", 
                    key, existing.getRequestCount(), maxRequests);
            return false;
        }
        
        // Increment count
        existing.setRequestCount(existing.getRequestCount() + 1);
        existing.setLastRequest(now);
        save(existing);
        
        log.debug("Incremented rate limit for key: {}, count: {}/{}", 
                key, existing.getRequestCount(), maxRequests);
        return true;
    }
    
    @Override
    public void reset(String key) {
        rateLimitRepository.deleteById(key);
        log.debug("Reset rate limit for key: {}", key);
    }
    
    @Override
    public RateLimitInfo getCurrentInfo(String key) {
        Optional<RateLimitEntity> entityOpt = findByKey(key);
        
        if (entityOpt.isEmpty()) {
            return RateLimitInfo.builder()
                    .key(key)
                    .currentRequests(0)
                    .isAllowed(true)
                    .build();
        }
        
        RateLimitEntity entity = entityOpt.get();
        LocalDateTime now = LocalDateTime.now();
        Duration windowDuration = Duration.ofSeconds(entity.getTtl());
        LocalDateTime windowEnd = entity.getWindowStart().plus(windowDuration);
        
        return RateLimitInfo.builder()
                .key(key)
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
    
    @Override
    public List<RateLimitEntity> findByIdentifier(String identifier) {
        return rateLimitRepository.findByIdentifier(identifier);
    }
    
    @Override
    public void deleteByKey(String key) {
        rateLimitRepository.deleteById(key);
    }
    
    @Override
    public void deleteByIdentifier(String identifier) {
        rateLimitRepository.deleteByIdentifier(identifier);
    }
}