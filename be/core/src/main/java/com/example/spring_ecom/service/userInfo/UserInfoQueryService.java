package com.example.spring_ecom.service.userInfo;

import com.example.spring_ecom.domain.userInfo.UserInfo;
import com.example.spring_ecom.repository.database.userInfo.UserInfoEntityMapper;
import com.example.spring_ecom.repository.database.userInfo.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoQueryService {
    
    private final UserInfoRepository repository;
    private final UserInfoEntityMapper mapper;
    
    protected Optional<UserInfo> findByUserId(Long userId) {
        return repository.findByUserId(userId)
                .map(mapper::toDomain);
    }
    
    protected Optional<UserInfo> findByPhoneNumber(String phoneNumber) {
        return repository.findByPhoneNumber(phoneNumber)
                .map(mapper::toDomain);
    }
    
    protected boolean existsByUserId(Long userId) {
        return repository.existsByUserId(userId);
    }
    
    protected boolean existsByPhoneNumber(String phoneNumber) {
        return repository.existsByPhoneNumber(phoneNumber);
    }
}