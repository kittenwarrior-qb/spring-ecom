package com.example.spring_ecom.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private String eventId;
    private Long userId;
    private String email;
    private String username;
    private String verificationToken;
    private Instant timestamp;
    private String source;
}
