package com.example.spring_ecom.service.userInfo;

import com.example.spring_ecom.domain.userInfo.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public UserInfo createOrUpdateUserInfo(Long userId, String firstName, String lastName, 
                                          String phoneNumber, LocalDate dateOfBirth, String avatarUrl,
                                          String address, String ward, String district, String city, String postalCode) {
        return commandService.createOrUpdate(userId, firstName, lastName, phoneNumber, dateOfBirth, 
                                           avatarUrl, address, ward, district, city, postalCode);
    }
    
    @Override
    @Transactional
    public UserInfo updateProfile(Long userId, String firstName, String lastName, 
                                 String phoneNumber, LocalDate dateOfBirth) {
        return commandService.updateProfile(userId, firstName, lastName, phoneNumber, dateOfBirth);
    }
    
    @Override
    @Transactional
    public UserInfo updateAvatar(Long userId, String avatarUrl) {
        return commandService.updateAvatar(userId, avatarUrl);
    }
    
    @Override
    @Transactional
    public UserInfo updateAddress(Long userId, String address, String ward, String district, String city, String postalCode) {
        return commandService.updateAddress(userId, address, ward, district, city, postalCode);
    }
    
    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        commandService.softDelete(userId);
    }
}