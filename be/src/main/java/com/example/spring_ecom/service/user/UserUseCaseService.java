package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserUseCaseService implements UserUseCase {
    private final UserQueryService queryService;

    @Transactional
    public Optional<User> findByUserId(Long userId){
        return queryService.findById(userId);
    }
}
