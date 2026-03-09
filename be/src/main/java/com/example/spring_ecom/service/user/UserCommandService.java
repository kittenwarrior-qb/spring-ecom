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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository repository;
    private final UserEntityMapper mapper;
    private final PasswordUtil passwordUtil;

    protected void save(User user) {
        // Check email
        if (repository.existsByEmail(user.email())) {
            log.warn("User with email {} already exists", user.email());
            throw new BaseException(ResponseCode.USER_ALREADY_EXISTS, "Email already exists");
        }
        // Check username
        if (user.username() != null && repository.existsByUsername(user.username())) {
            log.warn("User with username {} already exists", user.username());
            throw new BaseException(ResponseCode.USER_ALREADY_EXISTS, "Username already exists");
        }

        try {
            UserEntity newEntity = mapper.toEntity(user);
            if (newEntity.getIsActive() == null) {
                newEntity.setIsActive(true);
            }
            if (newEntity.getIsEmailVerified() == null) {
                newEntity.setIsEmailVerified(false);
            }
            if (newEntity.getRole() == null) {
                newEntity.setRole(com.example.spring_ecom.domain.user.UserRole.USER);
            }
            
            String encodedPassword = passwordUtil.encode(newEntity.getPassword());
            newEntity.setPassword(encodedPassword);
            repository.save(newEntity);
            log.info("User saved successfully with ID: {}", newEntity.getId());
        } catch (DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException: {}", e.getMostSpecificCause().getMessage(), e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to save user: " + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while saving user: {}", user.email(), e);
            throw e;
        }
    }
}

