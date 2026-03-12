package com.example.spring_ecom.service.userInfo;

import com.example.spring_ecom.domain.userInfo.UserInfo;

import java.time.LocalDate;
import java.util.Optional;

public interface UserInfoUseCase {
    
    Optional<UserInfo> findByUserId(Long userId);
    
    UserInfo createOrUpdateUserInfo(Long userId, String firstName, String lastName, 
                                   String phoneNumber, LocalDate dateOfBirth, String avatarUrl,
                                   String address, String ward, String district, String city, String postalCode);
    
    UserInfo updateProfile(Long userId, String firstName, String lastName, 
                          String phoneNumber, LocalDate dateOfBirth);
    
    UserInfo updateAvatar(Long userId, String avatarUrl);
    
    UserInfo updateAddress(Long userId, String address, String ward, String district, String city, String postalCode);
    
    void deleteByUserId(Long userId);
}