package com.example.spring_ecom.repository.redis.rateLimit.dao;

import com.example.spring_ecom.repository.redis.rateLimit.RateLimitEntity;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface RateLimitDao {
    
    RateLimitEntity save(RateLimitEntity entity);

    Optional<RateLimitEntity> findByKey(String key);

    String createKey(String rateLimitType, String endpoint, String identifier);

    boolean checkAndIncrement(String key, int maxRequests, Duration windowDuration);

    void reset(String key);

    RateLimitInfo getCurrentInfo(String key);

    List<RateLimitEntity> findByIdentifier(String identifier);

    void deleteByKey(String key);

    void deleteByIdentifier(String identifier);
}