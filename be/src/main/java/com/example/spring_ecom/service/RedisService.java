package com.example.spring_ecom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Set value with expiration time
     */
    public void set(String key, Object value, Duration timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout);
            log.debug("Set key: {} with timeout: {}", key, timeout);
        } catch (Exception e) {
            log.error("Error setting key: {} - {}", key, e.getMessage());
        }
    }

    /**
     * Set value without expiration
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("Set key: {}", key);
        } catch (Exception e) {
            log.error("Error setting key: {} - {}", key, e.getMessage());
        }
    }

    /**
     * Get value by key
     */
    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.debug("Get key: {} - value exists: {}", key, value != null);
            return value;
        } catch (Exception e) {
            log.error("Error getting key: {} - {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Get value by key with type casting
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        try {
            Object value = get(key);
            if (value != null && type.isInstance(value)) {
                return (T) value;
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting key: {} with type: {} - {}", key, type.getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * Delete key
     */
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            log.debug("Delete key: {} - success: {}", key, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error deleting key: {} - {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Check if key exists
     */
    public boolean hasKey(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error checking key existence: {} - {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Set expiration for existing key
     */
    public boolean expire(String key, Duration timeout) {
        try {
            Boolean result = redisTemplate.expire(key, timeout);
            log.debug("Set expiration for key: {} - timeout: {} - success: {}", key, timeout, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting expiration for key: {} - {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Get remaining time to live for key
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire != null ? expire : -1;
        } catch (Exception e) {
            log.error("Error getting expiration for key: {} - {}", key, e.getMessage());
            return -1;
        }
    }

    /**
     * Increment value by delta
     */
    public long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("Error incrementing key: {} - {}", key, e.getMessage());
            return 0;
        }
    }

    /**
     * Increment value by 1
     */
    public long increment(String key) {
        return increment(key, 1);
    }
}