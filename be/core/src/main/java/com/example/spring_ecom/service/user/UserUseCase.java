package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.domain.userInfo.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface UserUseCase {
    void save(User user);

    Optional<User> findByUserId(Long userId);

    Page<User> findAll(PageRequest pageRequest);
    
    void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword);
    
    // New methods for gRPC
    boolean updateUserStatus(Long userId, boolean isActive);
    
    boolean deleteUser(Long userId);
    
    Optional<UserInfo> getUserInfo(Long userId);
    
    Optional<UserInfo> updateUserInfo(UserInfo userInfo);
}
