package com.example.spring_ecom.service.userInfo;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import com.example.spring_ecom.repository.database.userInfo.UserInfoEntity;
import com.example.spring_ecom.repository.database.userInfo.UserInfoEntityMapper;
import com.example.spring_ecom.repository.database.userInfo.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoCommandService {
    
    private final UserInfoRepository repository;
    private final UserInfoEntityMapper mapper;
    
    // ========================== MAIN METHODS ================================
    
    public Optional<UserInfo> create(UserInfo userInfo) {
        validateUserInfo(userInfo);
        
        UserInfoEntity entity = mapper.toEntity(userInfo);
        
        repository.save(entity);
        return Optional.of(mapper.toDomain(entity));
    }
    
    public Optional<UserInfo> update(Long userId, UserInfo userInfo) {
        UserInfoEntity entity = findActiveUserInfoByUserId(userId);
        
        validateUserInfo(userInfo);
        
        mapper.update(entity, userInfo);
        
        repository.save(entity);
        return Optional.of(mapper.toDomain(entity));
    }
    
    public void delete(Long userId) {
        UserInfoEntity entity = findActiveUserInfoByUserId(userId);
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }
    
    
    
    // ========================== SUPPORT METHODS ================================
    
    private UserInfoEntity findActiveUserInfoByUserId(Long userId) {
        return repository.findByUserId(userId)
                .filter(e -> Objects.isNull(e.getDeletedAt()))
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "User info not found"));
    }
    
    private void validateUserInfo(UserInfo userInfo) {
        // Add validation logic here if needed
        // For example: validate phone number format, date of birth, etc.
    }
}