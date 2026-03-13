package com.example.spring_ecom.service.rateLimit;

import com.example.spring_ecom.repository.redis.rateLimit.RateLimitEntity;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitDao;
import com.example.spring_ecom.repository.redis.rateLimit.dao.RateLimitInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {
    
    private final RateLimitDao rateLimitDao;
    
    // Rate limit configurations
    private static final int LOGIN_MAX_REQUESTS = 5;
    private static final Duration LOGIN_WINDOW = Duration.ofMinutes(15);
    
    private static final int REGISTER_MAX_REQUESTS = 3;
    private static final Duration REGISTER_WINDOW = Duration.ofMinutes(30);
    
    private static final int API_MAX_REQUESTS = 100;
    private static final Duration API_WINDOW = Duration.ofMinutes(1);
    
    /**
     * Check rate limit cho login endpoint theo IP
     */
    public boolean checkLoginRateLimit(String ipAddress) {
        String key = rateLimitDao.createKey("IP", "login", ipAddress);
        return rateLimitDao.checkAndIncrement(key, LOGIN_MAX_REQUESTS, LOGIN_WINDOW);
    }
    
    /**
     * Check rate limit cho register endpoint theo IP
     */
    public boolean checkRegisterRateLimit(String ipAddress) {
        String key = rateLimitDao.createKey("IP", "register", ipAddress);
        return rateLimitDao.checkAndIncrement(key, REGISTER_MAX_REQUESTS, REGISTER_WINDOW);
    }
    
    /**
     * Check rate limit cho API calls theo user ID
     */
    public boolean checkApiRateLimit(Long userId) {
        String key = rateLimitDao.createKey("USER", "api", userId.toString());
        return rateLimitDao.checkAndIncrement(key, API_MAX_REQUESTS, API_WINDOW);
    }
    
    /**
     * Check rate limit tùy chỉnh
     */
    public boolean checkCustomRateLimit(String rateLimitType, String endpoint, String identifier, 
                                      int maxRequests, Duration windowDuration) {
        String key = rateLimitDao.createKey(rateLimitType, endpoint, identifier);
        return rateLimitDao.checkAndIncrement(key, maxRequests, windowDuration);
    }
    
    /**
     * Lấy thông tin rate limit hiện tại cho login
     */
    public RateLimitInfo getLoginRateLimitInfo(String ipAddress) {
        String key = rateLimitDao.createKey("IP", "login", ipAddress);
        return rateLimitDao.getCurrentInfo(key);
    }
    
    /**
     * Lấy thông tin rate limit hiện tại cho register
     */
    public RateLimitInfo getRegisterRateLimitInfo(String ipAddress) {
        String key = rateLimitDao.createKey("IP", "register", ipAddress);
        return rateLimitDao.getCurrentInfo(key);
    }
    
    /**
     * Lấy thông tin rate limit hiện tại cho API
     */
    public RateLimitInfo getApiRateLimitInfo(Long userId) {
        String key = rateLimitDao.createKey("USER", "api", userId.toString());
        return rateLimitDao.getCurrentInfo(key);
    }
    
    /**
     * Reset rate limit cho login
     */
    public void resetLoginRateLimit(String ipAddress) {
        String key = rateLimitDao.createKey("IP", "login", ipAddress);
        rateLimitDao.reset(key);
        log.info("Reset login rate limit for IP: {}", ipAddress);
    }
    
    /**
     * Reset rate limit cho register
     */
    public void resetRegisterRateLimit(String ipAddress) {
        String key = rateLimitDao.createKey("IP", "register", ipAddress);
        rateLimitDao.reset(key);
        log.info("Reset register rate limit for IP: {}", ipAddress);
    }
    
    /**
     * Reset tất cả rate limit của user
     */
    public void resetUserRateLimit(Long userId) {
        rateLimitDao.deleteByIdentifier(userId.toString());
        log.info("Reset all rate limits for user: {}", userId);
    }
    
    /**
     * Reset tất cả rate limit của IP
     */
    public void resetIpRateLimit(String ipAddress) {
        rateLimitDao.deleteByIdentifier(ipAddress);
        log.info("Reset all rate limits for IP: {}", ipAddress);
    }
    
    /**
     * Lấy danh sách rate limit của identifier
     */
    public List<RateLimitEntity> getRateLimitsByIdentifier(String identifier) {
        return rateLimitDao.findByIdentifier(identifier);
    }
    
    /**
     * Check xem có bị rate limit không và trả về thông tin chi tiết
     */
    public RateLimitResult checkRateLimitWithDetails(String rateLimitType, String endpoint, 
                                                   String identifier, int maxRequests, Duration windowDuration) {
        String key = rateLimitDao.createKey(rateLimitType, endpoint, identifier);
        RateLimitInfo currentInfo = rateLimitDao.getCurrentInfo(key);
        
        boolean isAllowed = rateLimitDao.checkAndIncrement(key, maxRequests, windowDuration);
        
        // Lấy thông tin updated sau khi increment
        RateLimitInfo updatedInfo = rateLimitDao.getCurrentInfo(key);
        
        return RateLimitResult.builder()
                .isAllowed(isAllowed)
                .rateLimitInfo(updatedInfo)
                .build();
    }
}