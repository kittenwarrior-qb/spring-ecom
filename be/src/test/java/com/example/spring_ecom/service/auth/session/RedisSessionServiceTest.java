package com.example.spring_ecom.service.auth.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisSessionServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RedisSessionService redisSessionService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        redisSessionService = new RedisSessionService(redisTemplate, objectMapper);
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldCreateSession() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        String role = "USER";
        String deviceInfo = "Chrome";
        String ipAddress = "127.0.0.1";

        // When
        String sessionId = redisSessionService.createSession(userId, email, role, deviceInfo, ipAddress);

        // Then
        assertNotNull(sessionId);
        verify(valueOperations, times(2)).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void shouldValidateSession() {
        // Given
        String sessionId = "test-session-id";
        when(redisTemplate.hasKey("session:" + sessionId)).thenReturn(true);

        // When
        boolean isValid = redisSessionService.isSessionValid(sessionId);

        // Then
        assertTrue(isValid);
        verify(redisTemplate).hasKey("session:" + sessionId);
    }
}