package com.example.spring_ecom.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {
    private String secret = "your-secret-key-change-this-in-production-minimum-256-bits";
    private Long expiration = 900000L; // 15 minutes for access token
    private Long refreshExpiration = 604800000L; // 7 days for refresh token
}
