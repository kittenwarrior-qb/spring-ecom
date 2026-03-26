package com.example.spring_ecom.repository.redis.rateLimit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateLimitRepository extends CrudRepository<RateLimitEntity, String> {
    
    List<RateLimitEntity> findByIdentifier(String identifier);

    Optional<RateLimitEntity> findByIdentifierAndEndpoint(String identifier, String endpoint);
    List<RateLimitEntity> findByEndpoint(String endpoint);

    List<RateLimitEntity> findByRateLimitType(String rateLimitType);

    void deleteByIdentifier(String identifier);

    void deleteByIdentifierAndEndpoint(String identifier, String endpoint);
}