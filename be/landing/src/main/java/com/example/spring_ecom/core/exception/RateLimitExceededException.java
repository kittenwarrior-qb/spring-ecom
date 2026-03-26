package com.example.spring_ecom.core.exception;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {
    
    private final String rateLimitType;
    private final String identifier;
    private final String endpoint;
    private final int currentRequests;
    private final int maxRequests;
    private final long remainingTimeSeconds;
    
    public RateLimitExceededException(String message, String rateLimitType, String identifier, 
                                   String endpoint, int currentRequests, int maxRequests, 
                                   long remainingTimeSeconds) {
        super(message);
        this.rateLimitType = rateLimitType;
        this.identifier = identifier;
        this.endpoint = endpoint;
        this.currentRequests = currentRequests;
        this.maxRequests = maxRequests;
        this.remainingTimeSeconds = remainingTimeSeconds;
    }
    
    public RateLimitExceededException(String message) {
        super(message);
        this.rateLimitType = null;
        this.identifier = null;
        this.endpoint = null;
        this.currentRequests = 0;
        this.maxRequests = 0;
        this.remainingTimeSeconds = 0;
    }
}