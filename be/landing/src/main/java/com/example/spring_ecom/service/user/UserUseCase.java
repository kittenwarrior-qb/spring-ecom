package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface UserUseCase {
    void save(User user);

    Optional<User> findByUserId(Long userId);

    Page<User> findAll(PageRequest pageRequest);
    
    void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword);
}
