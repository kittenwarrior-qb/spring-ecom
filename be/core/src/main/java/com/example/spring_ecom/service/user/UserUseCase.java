package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserUseCase {
    void save(User user);

    Optional<User> findByUserId(Long userId);

    Page<User> findAll(PageRequest pageRequest);
    
    void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword);
    
    // New methods for gRPC
    boolean updateUserStatus(Long userId, boolean isActive);
    
    boolean deleteUser(Long userId);
    
    Optional<UserInfo> getUserInfo(Long userId);
    
    Optional<UserInfo> updateUserInfo(UserInfo userInfo);

    Page<User> searchByEmail(String email, PageRequest pageRequest);

    // ========== Auth-related methods (used by AuthCommandService, AuthQueryService, EmailCommandService) ==========

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void updateLastLogin(Long userId);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);

    void setEmailVerificationToken(Long userId, String token, LocalDateTime expiry);

    void markEmailVerified(Long userId);

    User createUserForRegistration(String username, String email, String encodedPassword);
}
