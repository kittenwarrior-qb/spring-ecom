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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoCommandService {
    
    private final UserInfoRepository repository;
    private final UserInfoEntityMapper mapper;
    
    protected UserInfo save(UserInfo userInfo) {
        UserInfoEntity entity = mapper.toEntity(userInfo);
        UserInfoEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    
    protected UserInfo createOrUpdate(Long userId, String firstName, String lastName, 
                                     String phoneNumber, LocalDate dateOfBirth, String avatarUrl,
                                     String address, String ward, String district, String city, String postalCode) {
        UserInfoEntity entity = repository.findByUserId(userId)
                .orElse(UserInfoEntity.builder()
                        .userId(userId)
                        .build());
        
        UserInfo userInfo = new UserInfo(
                null, // id will be set by entity
                userId,
                firstName,
                lastName,
                phoneNumber,
                dateOfBirth,
                avatarUrl,
                address,
                ward,
                district,
                city,
                postalCode,
                null, // createdAt will be set by entity
                null, // updatedAt will be set by entity
                null  // deletedAt will be set by entity
        );
        
        mapper.update(entity, userInfo);
        
        UserInfoEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    
    protected UserInfo updateProfile(Long userId, String firstName, String lastName, 
                                    String phoneNumber, LocalDate dateOfBirth) {
        UserInfoEntity entity = repository.findByUserId(userId)
                .orElse(UserInfoEntity.builder()
                        .userId(userId)
                        .build());
        
        UserInfo currentUserInfo = mapper.toDomain(entity);
        UserInfo updatedUserInfo = new UserInfo(
                currentUserInfo.id(),
                userId,
                firstName != null ? firstName : currentUserInfo.firstName(),
                lastName != null ? lastName : currentUserInfo.lastName(),
                phoneNumber != null ? phoneNumber : currentUserInfo.phoneNumber(),
                dateOfBirth != null ? dateOfBirth : currentUserInfo.dateOfBirth(),
                currentUserInfo.avatarUrl(),
                currentUserInfo.address(),
                currentUserInfo.ward(),
                currentUserInfo.district(),
                currentUserInfo.city(),
                currentUserInfo.postalCode(),
                currentUserInfo.createdAt(),
                currentUserInfo.updatedAt(),
                currentUserInfo.deletedAt()
        );
        
        mapper.update(entity, updatedUserInfo);
        
        UserInfoEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    
    protected UserInfo updateAvatar(Long userId, String avatarUrl) {
        UserInfoEntity entity = repository.findByUserId(userId)
                .orElse(UserInfoEntity.builder()
                        .userId(userId)
                        .build());
        
        UserInfo currentUserInfo = mapper.toDomain(entity);
        UserInfo updatedUserInfo = new UserInfo(
                currentUserInfo.id(),
                userId,
                currentUserInfo.firstName(),
                currentUserInfo.lastName(),
                currentUserInfo.phoneNumber(),
                currentUserInfo.dateOfBirth(),
                avatarUrl,
                currentUserInfo.address(),
                currentUserInfo.ward(),
                currentUserInfo.district(),
                currentUserInfo.city(),
                currentUserInfo.postalCode(),
                currentUserInfo.createdAt(),
                currentUserInfo.updatedAt(),
                currentUserInfo.deletedAt()
        );
        
        mapper.update(entity, updatedUserInfo);
        
        UserInfoEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    
    protected UserInfo updateAddress(Long userId, String address, String ward, String district, String city, String postalCode) {
        UserInfoEntity entity = repository.findByUserId(userId)
                .orElse(UserInfoEntity.builder()
                        .userId(userId)
                        .build());
        
        UserInfo currentUserInfo = mapper.toDomain(entity);
        UserInfo updatedUserInfo = new UserInfo(
                currentUserInfo.id(),
                userId,
                currentUserInfo.firstName(),
                currentUserInfo.lastName(),
                currentUserInfo.phoneNumber(),
                currentUserInfo.dateOfBirth(),
                currentUserInfo.avatarUrl(),
                address,
                ward,
                district,
                city,
                postalCode,
                currentUserInfo.createdAt(),
                currentUserInfo.updatedAt(),
                currentUserInfo.deletedAt()
        );
        
        mapper.update(entity, updatedUserInfo);
        
        UserInfoEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    
    protected void softDelete(Long userId) {
        UserInfoEntity entity = repository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "User info not found"));
        
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }
}