package com.example.spring_ecom.repository.redis.rateLimit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("rate_limit")
public class RateLimitEntity {
    
    @Id
    private String key; // rate_limit:login:192.168.1.1 hoặc rate_limit:user:123
    
    private String identifier; // IP address hoặc user ID
    private String endpoint; // /login, /register, etc.
    private String rateLimitType; // IP, USER, ENDPOINT
    private Integer requestCount; 
    private Integer maxRequests; 
    private LocalDateTime windowStart; 
    private LocalDateTime lastRequest; 
    private LocalDateTime createdAt;
    
    @TimeToLive
    private Long ttl; 
}