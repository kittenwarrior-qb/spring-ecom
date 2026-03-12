package com.example.spring_ecom.repository.redis.session;

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
@RedisHash("redis")
public class RedisEntity {
    
    @Id
    private String sessionId;
    
    private Long userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String district;
    private String ward;
    private String deviceInfo;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    
    @TimeToLive
    private Long ttl;
}