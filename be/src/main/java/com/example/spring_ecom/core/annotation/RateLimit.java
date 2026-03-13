package com.example.spring_ecom.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * Loại rate limit: IP, USER, ENDPOINT
     */
    String type() default "IP";
    
    /**
     * Tên endpoint để phân biệt
     */
    String endpoint() default "";
    
    /**
     * Số request tối đa trong window
     */
    int maxRequests() default 10;
    
    /**
     * Thời gian window (phút)
     */
    int windowMinutes() default 1;
    
    /**
     * Message khi bị rate limit
     */
    String message() default "Too many requests. Please try again later.";
}