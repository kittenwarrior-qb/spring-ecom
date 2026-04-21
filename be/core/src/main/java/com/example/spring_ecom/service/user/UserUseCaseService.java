package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import com.example.spring_ecom.service.userInfo.UserInfoUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUseCaseService implements UserUseCase {
    private final UserQueryService queryService;
    private final UserCommandService commandService;
    private final UserInfoUseCase userInfoUseCase;

    @Override
    @Transactional
    public Optional<User> findByUserId(Long userId){
        return queryService.findById(userId);
    }

    @Override
    @Transactional
    public Page<User> findAll(PageRequest pageRequest) {
        return queryService.findAll(pageRequest);
    }

    @Override
    @Transactional
    public void save(User user) {
        commandService.save(user);
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        commandService.changePassword(userId, currentPassword, newPassword, confirmPassword);
    }
    
    // New gRPC methods
    @Override
    @Transactional
    public boolean updateUserStatus(Long userId, boolean isActive) {
        try {
            // TODO: Implement user status update logic
            log.info("Updating user status for userId: {}, isActive: {}", userId, isActive);
            return true; // Placeholder
        } catch (Exception ex) {
            log.error("Error updating user status", ex);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        try {
            commandService.deleteUser(userId);
            return true;
        } catch (Exception ex) {
            log.error("Error deleting user", ex);
            return false;
        }
    }
    
    @Override
    @Transactional
    public Optional<UserInfo> getUserInfo(Long userId) {
        return userInfoUseCase.findByUserId(userId);
    }
    
    @Override
    @Transactional
    public Optional<UserInfo> updateUserInfo(UserInfo userInfo) {
        return userInfoUseCase.createOrUpdate(userInfo.userId(), userInfo);
    }

    @Override
    @Transactional
    public Page<User> searchByEmail(String email, PageRequest pageRequest) {
        return queryService.searchByEmail(email, pageRequest);
    }

    // ========== Auth-related methods ==========

    @Override
    @Transactional
    public Optional<User> findByEmail(String email) {
        return queryService.findByEmail(email);
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        return queryService.existsByEmail(email);
    }

    @Override
    @Transactional
    public boolean existsByUsername(String username) {
        return queryService.existsByUsername(username);
    }

    @Override
    @Transactional
    public void updateLastLogin(Long userId) {
        commandService.updateLastLogin(userId);
    }

    @Override
    @Transactional
    public Optional<User> findByEmailVerificationToken(String token) {
        return queryService.findByEmailVerificationToken(token);
    }

    @Override
    @Transactional
    public Optional<User> findByPasswordResetToken(String token) {
        return queryService.findByPasswordResetToken(token);
    }

    @Override
    @Transactional
    public void setEmailVerificationToken(Long userId, String token, LocalDateTime expiry) {
        commandService.setEmailVerificationToken(userId, token, expiry);
    }

    @Override
    @Transactional
    public void markEmailVerified(Long userId) {
        commandService.markEmailVerified(userId);
    }

    @Override
    @Transactional
    public User createUserForRegistration(String username, String email, String encodedPassword) {
        return commandService.createUserForRegistration(username, email, encodedPassword);
    }
}
