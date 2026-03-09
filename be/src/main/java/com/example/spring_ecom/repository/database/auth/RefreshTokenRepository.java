package com.example.spring_ecom.repository.database.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    
    Optional<RefreshTokenEntity> findByToken(String token);
    
    void deleteByUserId(Long userId);
    
    void deleteByExpiresAtBeforeOrRevokedAtIsNotNull(LocalDateTime now);
}
