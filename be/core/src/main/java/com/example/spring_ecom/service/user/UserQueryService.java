package com.example.spring_ecom.service.user;

import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.repository.database.user.UserEntityMapper;
import com.example.spring_ecom.repository.database.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

   protected Page<User> findAll(PageRequest pageRequest) {
       return repository.findAll(pageRequest).map(mapper::toDomain);
   }

   protected Page<User> searchByEmail(String email, PageRequest pageRequest) {
       return repository.findByEmailContainingIgnoreCase(email, pageRequest).map(mapper::toDomain);
   }

   // ========== Auth-related queries ==========

   protected Optional<User> findByEmail(String email) {
       return repository.findByEmail(email).map(mapper::toDomain);
   }

   protected boolean existsByEmail(String email) {
       return repository.existsByEmail(email);
   }

   protected boolean existsByUsername(String username) {
       return repository.existsByUsername(username);
   }

   protected Optional<User> findByEmailVerificationToken(String token) {
       return repository.findByEmailVerificationToken(token).map(mapper::toDomain);
   }

   protected Optional<User> findByPasswordResetToken(String token) {
       return repository.findByPasswordResetToken(token).map(mapper::toDomain);
   }
}
