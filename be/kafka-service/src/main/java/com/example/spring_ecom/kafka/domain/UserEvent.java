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

    private String eventId;
    private String eventType;   // REGISTERED | UPDATED | DELETED
    private Instant timestamp;
    private String source;      // "server"

    // User payload
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean isActive;

    // Event type constants
    public static final String REGISTERED = "USER_REGISTERED";
    public static final String UPDATED    = "USER_UPDATED";
    public static final String DELETED    = "USER_DELETED";
}
