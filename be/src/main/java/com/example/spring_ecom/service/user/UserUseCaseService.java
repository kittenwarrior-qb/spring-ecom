package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserEntityMapper;
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


    @Transactional
    public Optional<User> findByUserId(Long userId){
        return queryService.findById(userId);
    }

    @Transactional
    public Page<User> findAll(PageRequest pageRequest) {
        return queryService.findAll(pageRequest);
    }

    @Transactional
    public void save(User user) {
        commandService.save(user);
    }
    
    @Override
    @Transactional
    public User updateProfile(Long userId, String firstName, String lastName, String phoneNumber, java.time.LocalDate dateOfBirth) {
        return commandService.updateProfile(userId, firstName, lastName, phoneNumber, dateOfBirth);
    }
    
    @Override
    @Transactional
    public User updateAvatar(Long userId, String avatarUrl) {
        return commandService.updateAvatar(userId, avatarUrl);
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        commandService.changePassword(userId, currentPassword, newPassword);
    }
}
