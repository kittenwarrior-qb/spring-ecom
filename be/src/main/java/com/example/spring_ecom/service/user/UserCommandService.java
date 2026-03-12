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

    protected void save(User user) {
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

        try {
            UserEntity newEntity = mapper.toEntity(user);
            
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
    
    protected void changePassword(Long userId, String currentPassword, String newPassword) {
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
}