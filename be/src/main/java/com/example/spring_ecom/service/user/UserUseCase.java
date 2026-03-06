package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;

import java.util.Optional;

public interface UserUseCase {
    Optional<User> findByUserId(Long userId);
}
