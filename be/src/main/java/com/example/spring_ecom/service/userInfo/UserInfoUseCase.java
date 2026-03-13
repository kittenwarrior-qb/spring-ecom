package com.example.spring_ecom.service.userInfo;

import com.example.spring_ecom.domain.userInfo.UserInfo;

import java.util.Optional;

public interface UserInfoUseCase {
    
    Optional<UserInfo> findByUserId(Long userId);
    
    Optional<UserInfo> createOrUpdate(Long userId, UserInfo userInfo);
    
    void delete(Long userId);
}