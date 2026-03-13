package com.example.spring_ecom.repository.redis.rateLimit.dao;

import com.example.spring_ecom.repository.redis.rateLimit.RateLimitEntity;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface RateLimitDao {
    
    /**
     * Lưu hoặc update rate limit entity
     */
    RateLimitEntity save(RateLimitEntity entity);
    
    /**
     * Tìm rate limit theo key
     */
    Optional<RateLimitEntity> findByKey(String key);
    
    /**
     * Tạo rate limit key theo pattern
     */
    String createKey(String rateLimitType, String endpoint, String identifier);
    
    /**
     * Check và increment request count
     * @return true nếu request được phép, false nếu exceed limit
     */
    boolean checkAndIncrement(String key, int maxRequests, Duration windowDuration);
    
    /**
     * Reset rate limit cho key
     */
    void reset(String key);
    
    /**
     * Lấy thông tin rate limit hiện tại
     */
    RateLimitInfo getCurrentInfo(String key);
    
    /**
     * Lấy danh sách rate limit theo identifier
     */
    List<RateLimitEntity> findByIdentifier(String identifier);
    
    /**
     * Xóa rate limit theo key
     */
    void deleteByKey(String key);
    
    /**
     * Xóa tất cả rate limit của identifier
     */
    void deleteByIdentifier(String identifier);
}