package com.example.spring_ecom.repository.redis.session;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedisRepository extends CrudRepository<RedisEntity, String> {
    
    List<RedisEntity> findByUserId(Long userId);
    
    Optional<RedisEntity> findBySessionId(String sessionId);
    
    void deleteByUserId(Long userId);
}