package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserEntityMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     public void save(User user) {
        commandService.save(user);
     }
}
