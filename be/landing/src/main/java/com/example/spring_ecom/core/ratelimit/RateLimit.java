package com.example.spring_ecom.core.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * Annotation để config rate limit cho endpoint
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    int limit() default 100;
    
    int duration() default 1;
    
    /**
     * Đơn vị thời gian
     */
    ChronoUnit unit() default ChronoUnit.MINUTES;
    
    /**
     * Loại rate limit: IP, USER, ENDPOINT
     */
    RateLimitType type() default RateLimitType.IP;
    
    /**
     * Message khi bị rate limit
     */
    String message() default "Too many requests. Please try again later.";
}