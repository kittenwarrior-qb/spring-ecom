package com.example.spring_ecom.repository.redis.rateLimit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateLimitRepository extends CrudRepository<RateLimitEntity, String> {
    
    /**
     * Tìm rate limit theo identifier (IP hoặc user ID)
     */
    List<RateLimitEntity> findByIdentifier(String identifier);
    
    /**
     * Tìm rate limit theo identifier và endpoint
     */
    Optional<RateLimitEntity> findByIdentifierAndEndpoint(String identifier, String endpoint);
    
    /**
     * Tìm rate limit theo endpoint
     */
    List<RateLimitEntity> findByEndpoint(String endpoint);
    
    /**
     * Tìm rate limit theo type
     */
    List<RateLimitEntity> findByRateLimitType(String rateLimitType);
    
    /**
     * Xóa rate limit theo identifier
     */
    void deleteByIdentifier(String identifier);
    
    /**
     * Xóa rate limit theo identifier và endpoint
     */
    void deleteByIdentifierAndEndpoint(String identifier, String endpoint);
}