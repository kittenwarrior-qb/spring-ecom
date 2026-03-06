package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.repository.database.user.UserEntityMapper;
import com.example.spring_ecom.repository.database.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository repository;
    private final UserEntityMapper mapper;

   protected Optional<User> findById(Long userId) {
       return repository.findById(userId).map(mapper::toDomain);
   }
}
