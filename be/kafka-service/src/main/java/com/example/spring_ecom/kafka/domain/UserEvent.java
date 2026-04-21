package com.example.spring_ecom.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    // Event constants
    public static final String REGISTERED = "USER_REGISTERED";
    
    private String eventId;
    private Long userId;
    private String email;
    private String eventType;
    private Instant timestamp;
}
