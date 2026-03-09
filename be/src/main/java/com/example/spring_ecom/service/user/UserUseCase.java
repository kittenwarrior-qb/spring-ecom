package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;

import java.util.Optional;

public interface UserUseCase {
    void save(User user);

    Optional<User> findByUserId(Long userId);


}
