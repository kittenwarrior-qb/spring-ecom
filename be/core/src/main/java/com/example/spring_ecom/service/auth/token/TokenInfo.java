package com.example.spring_ecom.service.auth.token;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenInfo {
    private String sessionId;
    private Long userId;
    private String email;
    private String role;
    private List<String> authorities;
}