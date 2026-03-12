package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUseCaseService implements UserUseCase {
    private final UserQueryService queryService;
    private final UserCommandService commandService;

    @Override
    @Transactional
    public Optional<User> findByUserId(Long userId){
        return queryService.findById(userId);
    }

    @Override
    @Transactional
    public Page<User> findAll(PageRequest pageRequest) {
        return queryService.findAll(pageRequest);
    }

    @Override
    @Transactional
    public void save(User user) {
        commandService.save(user);
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        commandService.changePassword(userId, currentPassword, newPassword);
    }
}
