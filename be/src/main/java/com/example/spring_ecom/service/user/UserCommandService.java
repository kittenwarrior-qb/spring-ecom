package com.example.spring_ecom.service.user;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.core.util.PasswordUtil;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.user.UserRole;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserEntityMapper;
import com.example.spring_ecom.repository.database.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository repository;
    private final UserEntityMapper mapper;
    private final PasswordUtil passwordUtil;

    protected void save(User user) {
        // Validate required fields
        if (Objects.isNull(user.email()) || user.email().trim().isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Email is required");
        }
        
        // Check email uniqueness
        if (repository.existsByEmail(user.email())) {
            log.warn("User with email {} already exists", user.email());
            throw new BaseException(ResponseCode.USER_ALREADY_EXISTS, "Email already exists");
        }
        
        // Check username uniqueness if provided
        if (Objects.nonNull(user.username()) && repository.existsByUsername(user.username())) {
            log.warn("User with username {} already exists", user.username());
            throw new BaseException(ResponseCode.USER_ALREADY_EXISTS, "Username already exists");
        }

        try {
            UserEntity newEntity = mapper.toEntity(user);
            
            // Set default values using Objects.isNull for better null safety
            if (Objects.isNull(newEntity.getIsActive())) {
                newEntity.setIsActive(true);
            }
            if (Objects.isNull(newEntity.getIsEmailVerified())) {
                newEntity.setIsEmailVerified(false);
            }
            if (Objects.isNull(newEntity.getRole())) {
                newEntity.setRole(UserRole.USER);
            }
            
            // Encode password
            String encodedPassword = passwordUtil.encode(newEntity.getPassword());
            newEntity.setPassword(encodedPassword);
            
            UserEntity savedEntity = repository.save(newEntity);
            log.info("User saved successfully with ID: {}", savedEntity.getId());
            
        } catch (DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException: {}", e.getMostSpecificCause().getMessage(), e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, 
                "Failed to save user: " + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while saving user: {}", user.email(), e);
            throw e;
        }
    }
    
    protected User updateProfile(Long userId, String firstName, String lastName, String phoneNumber, LocalDate dateOfBirth) {
        UserEntity entity = repository.findById(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        
        // Update fields only if they are not null
        if (Objects.nonNull(firstName)) {
            entity.setFirstName(firstName);
        }
        if (Objects.nonNull(lastName)) {
            entity.setLastName(lastName);
        }
        if (Objects.nonNull(phoneNumber)) {
            entity.setPhoneNumber(phoneNumber);
        }
        if (Objects.nonNull(dateOfBirth)) {
            entity.setDateOfBirth(dateOfBirth);
        }
        
        UserEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected User updateAvatar(Long userId, String avatarUrl) {
        UserEntity entity = repository.findById(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        
        entity.setAvatarUrl(avatarUrl);
        UserEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    protected void changePassword(Long userId, String currentPassword, String newPassword) {
        // Validate input parameters
        if (Objects.isNull(currentPassword) || currentPassword.trim().isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Current password is required");
        }
        if (Objects.isNull(newPassword) || newPassword.trim().isEmpty()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "New password is required");
        }
        
        UserEntity entity = repository.findById(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        
        // Verify current password
        if (!passwordUtil.matches(currentPassword, entity.getPassword())) {
            throw new BaseException(ResponseCode.INVALID_CREDENTIALS, "Current password is incorrect");
        }
        
        // Update to new password
        String encodedPassword = passwordUtil.encode(newPassword);
        entity.setPassword(encodedPassword);
        repository.save(entity);
        log.info("Password changed successfully for user ID: {}", userId);
    }
    
    // === Methods using SecurityContext - không cần truyền userId ===
    
    protected User updateCurrentUserProfile(String firstName, String lastName, String phoneNumber, LocalDate dateOfBirth) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (Objects.isNull(currentUserId)) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "User not authenticated");
        }
        return updateProfile(currentUserId, firstName, lastName, phoneNumber, dateOfBirth);
    }
    
    protected User updateCurrentUserAvatar(String avatarUrl) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (Objects.isNull(currentUserId)) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "User not authenticated");
        }
        return updateAvatar(currentUserId, avatarUrl);
    }
    
    protected void changeCurrentUserPassword(String currentPassword, String newPassword) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (Objects.isNull(currentUserId)) {
            throw new BaseException(ResponseCode.UNAUTHORIZED, "User not authenticated");
        }
        changePassword(currentUserId, currentPassword, newPassword);
    }
}