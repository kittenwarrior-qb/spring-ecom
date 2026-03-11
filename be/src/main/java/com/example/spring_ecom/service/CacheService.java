package com.example.spring_ecom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisService redisService;

    // Cache prefixes
    private static final String USER_CACHE_PREFIX = "user:";
    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final String CATEGORY_CACHE_PREFIX = "category:";
    private static final String CART_CACHE_PREFIX = "cart:";
    private static final String SESSION_CACHE_PREFIX = "session:";

    // Cache durations
    private static final Duration USER_CACHE_DURATION = Duration.ofHours(1);
    private static final Duration PRODUCT_CACHE_DURATION = Duration.ofMinutes(30);
    private static final Duration CATEGORY_CACHE_DURATION = Duration.ofHours(2);
    private static final Duration CART_CACHE_DURATION = Duration.ofHours(24);
    private static final Duration SESSION_CACHE_DURATION = Duration.ofHours(24);

    // User cache methods
    public void cacheUser(Long userId, Object user) {
        String key = USER_CACHE_PREFIX + userId;
        redisService.set(key, user, USER_CACHE_DURATION);
    }

    public <T> T getCachedUser(Long userId, Class<T> type) {
        String key = USER_CACHE_PREFIX + userId;
        return redisService.get(key, type);
    }

    public void evictUser(Long userId) {
        String key = USER_CACHE_PREFIX + userId;
        redisService.delete(key);
    }

    // Product cache methods
    public void cacheProduct(Long productId, Object product) {
        String key = PRODUCT_CACHE_PREFIX + productId;
        redisService.set(key, product, PRODUCT_CACHE_DURATION);
    }

    public <T> T getCachedProduct(Long productId, Class<T> type) {
        String key = PRODUCT_CACHE_PREFIX + productId;
        return redisService.get(key, type);
    }

    public void evictProduct(Long productId) {
        String key = PRODUCT_CACHE_PREFIX + productId;
        redisService.delete(key);
    }

    // Category cache methods
    public void cacheCategory(Long categoryId, Object category) {
        String key = CATEGORY_CACHE_PREFIX + categoryId;
        redisService.set(key, category, CATEGORY_CACHE_DURATION);
    }

    public <T> T getCachedCategory(Long categoryId, Class<T> type) {
        String key = CATEGORY_CACHE_PREFIX + categoryId;
        return redisService.get(key, type);
    }

    public void evictCategory(Long categoryId) {
        String key = CATEGORY_CACHE_PREFIX + categoryId;
        redisService.delete(key);
    }

    // Cart cache methods
    public void cacheCart(Long userId, Object cart) {
        String key = CART_CACHE_PREFIX + userId;
        redisService.set(key, cart, CART_CACHE_DURATION);
    }

    public <T> T getCachedCart(Long userId, Class<T> type) {
        String key = CART_CACHE_PREFIX + userId;
        return redisService.get(key, type);
    }

    public void evictCart(Long userId) {
        String key = CART_CACHE_PREFIX + userId;
        redisService.delete(key);
    }

    // Session cache methods
    public void cacheSession(String sessionId, Object sessionData) {
        String key = SESSION_CACHE_PREFIX + sessionId;
        redisService.set(key, sessionData, SESSION_CACHE_DURATION);
    }

    public <T> T getCachedSession(String sessionId, Class<T> type) {
        String key = SESSION_CACHE_PREFIX + sessionId;
        return redisService.get(key, type);
    }

    public void evictSession(String sessionId) {
        String key = SESSION_CACHE_PREFIX + sessionId;
        redisService.delete(key);
    }

    // Rate limiting methods
    public boolean isRateLimited(String identifier, int maxRequests, Duration window) {
        String key = "rate_limit:" + identifier;
        long currentCount = redisService.increment(key);
        
        if (currentCount == 1) {
            // First request, set expiration
            redisService.expire(key, window);
        }
        
        return currentCount > maxRequests;
    }

    // Generic cache methods
    public void cache(String key, Object value, Duration duration) {
        redisService.set(key, value, duration);
    }

    public <T> T getCache(String key, Class<T> type) {
        return redisService.get(key, type);
    }

    public void evictCache(String key) {
        redisService.delete(key);
    }
}