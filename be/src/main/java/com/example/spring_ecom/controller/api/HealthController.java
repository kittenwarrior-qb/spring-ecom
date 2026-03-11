package com.example.spring_ecom.controller.api;

import com.example.spring_ecom.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final RedisService redisService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        
        // Test Redis connection
        try {
            String testKey = "health_check_" + System.currentTimeMillis();
            String testValue = "OK";
            
            redisService.set(testKey, testValue, Duration.ofSeconds(10));
            Object retrievedValue = redisService.get(testKey);
            redisService.delete(testKey);
            
            if (testValue.equals(retrievedValue)) {
                health.put("redis", "UP");
            } else {
                health.put("redis", "DOWN - Value mismatch");
            }
        } catch (Exception e) {
            health.put("redis", "DOWN - " + e.getMessage());
        }
        
        return ResponseEntity.ok(health);
    }
}