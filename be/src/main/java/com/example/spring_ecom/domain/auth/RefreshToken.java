package com.example.spring_ecom.domain.auth;

import java.time.LocalDateTime;

public record RefreshToken(
    Long id,
    Long userId,
    String token,
    LocalDateTime expiresAt,
    LocalDateTime createdAt,
    LocalDateTime revokedAt,
    String replacedByToken,
    String deviceInfo,
    String ipAddress
) {
}
