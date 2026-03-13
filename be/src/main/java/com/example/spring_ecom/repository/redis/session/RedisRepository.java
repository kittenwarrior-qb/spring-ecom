package com.example.spring_ecom.repository.redis.session;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedisRepository extends CrudRepository<RedisEntity, String> {
    
    List<RedisEntity> findByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}