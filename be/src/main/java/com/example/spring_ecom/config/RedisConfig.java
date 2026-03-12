package com.example.spring_ecom.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    
    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        try {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            
            // Use String serializer for both keys and values for simplicity
            template.setKeySerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new StringRedisSerializer());
            
            template.afterPropertiesSet();
            
            // Log Redis connection info
            logger.info("----------------------------------------------------------------------");
            logger.info("Redis connection established and running on port: {}", redisPort);
            logger.info("Redis configuration loaded successfully");
            logger.info("----------------------------------------------------------------------");
            
            return template;
        } catch (Exception e) {
            logger.error("----------------------------------------------------------------------");
            logger.error("Failed to establish Redis connection on port: {}", redisPort);
            logger.error("Redis connection error: {}", e.getMessage());
            logger.error("----------------------------------------------------------------------");
            throw new RuntimeException("Redis configuration failed", e);
        }
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}