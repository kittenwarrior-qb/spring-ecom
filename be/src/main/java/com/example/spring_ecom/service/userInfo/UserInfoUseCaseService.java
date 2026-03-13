package com.example.spring_ecom.service.userInfo;

import com.example.spring_ecom.domain.userInfo.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoUseCaseService implements UserInfoUseCase {
    
    private final UserInfoQueryService queryService;
    private final UserInfoCommandService commandService;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserInfo> findByUserId(Long userId) {
        return queryService.findByUserId(userId);
    }
    
    @Override
    @Transactional
    public Optional<UserInfo> createOrUpdate(Long userId, UserInfo userInfo) {
        Optional<UserInfo> existing = queryService.findByUserId(userId);
        
        if (existing.isPresent()) {
            return commandService.update(userId, userInfo);
        } else {
            return commandService.create(userInfo);
        }
    }
    
    @Override
    @Transactional
    public void delete(Long userId) {
        commandService.delete(userId);
    }
}