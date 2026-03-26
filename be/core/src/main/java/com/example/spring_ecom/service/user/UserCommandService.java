package com.example.spring_ecom.service.user;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.PasswordUtil;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserEntityMapper;
import com.example.spring_ecom.repository.database.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository repository;
    private final UserEntityMapper mapper;
    private final PasswordUtil passwordUtil;

    // ========================== MAIN METHODS ================================

    protected void save(User user) {
        validateUserData(user);
        
        UserEntity newEntity = mapper.toEntity(user);
        String encodedPassword = passwordUtil.encode(newEntity.getPassword());
        mapper.updatePassword(newEntity, encodedPassword);
        
        saveUserEntity(newEntity);
    }
    
    protected void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        validatePasswordChangeRequest(currentPassword, newPassword, confirmPassword);
        
        UserEntity entity = findUserById(userId);
        validateCurrentPassword(currentPassword, entity.getPassword());
        
        String encodedPassword = passwordUtil.encode(newPassword);
        mapper.updatePassword(entity, encodedPassword);
        repository.save(entity);
        log.info("Password changed successfully for user ID: {}", userId);
    }

    protected void deleteUser(Long userId) {
        UserEntity entity = findUserById(userId);
        entity.setDeletedAt(java.time.LocalDateTime.now());
        entity.setIsActive(false);
        repository.save(entity);
        log.info("User {} completely soft-deleted from the database.", userId);
    }

    // ========================== SUPPORT METHODS ================================

    private void validateUserData(User user) {
        if (Objects.isNull(user.email()) || user.email().trim().isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Email is required");
        }
        if (repository.existsByEmail(user.email())) {
            log.warn("User with email {} already exists", user.email());
            throw new BaseException(ResponseCode.USER_ALREADY_EXISTS, "Email already exists");
        }
        if (Objects.nonNull(user.username()) && repository.existsByUsername(user.username())) {
            log.warn("User with username {} already exists", user.username());
            throw new BaseException(ResponseCode.USER_ALREADY_EXISTS, "Username already exists");
        }
    }
    
    private void saveUserEntity(UserEntity entity) {
        try {
            UserEntity savedEntity = repository.save(entity);
            log.info("User saved successfully with ID: {}", savedEntity.getId());
        } catch (DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException: {}", e.getMostSpecificCause().getMessage(), e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, 
                "Failed to save user: " + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while saving user", e);
            throw e;
        }
    }
    
    private void validatePasswordChangeRequest(String currentPassword, String newPassword, String confirmPassword) {
        if (Objects.isNull(currentPassword) || currentPassword.trim().isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Current password is required");
        }
        if (Objects.isNull(newPassword) || newPassword.trim().isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "New password is required");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "New password and confirm password do not match");
        }
    }
    
    private UserEntity findUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
    }
    
    private void validateCurrentPassword(String currentPassword, String storedPassword) {
        if (!passwordUtil.matches(currentPassword, storedPassword)) {
            throw new BaseException(ResponseCode.INVALID_CREDENTIALS, "Current password is incorrect");
        }
    }
}