package com.example.spring_ecom.repository.database.auth;

import com.example.spring_ecom.repository.database.common.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity extends BaseTimestampEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    
    @Column(name = "replaced_by_token", length = 500)
    private String replacedByToken;
    
    @Column(name = "device_info", length = 255)
    private String deviceInfo;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}
