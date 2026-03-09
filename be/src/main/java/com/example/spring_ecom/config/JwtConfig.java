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
    private Long expiration = 86400000L;
}
