package com.example.spring_ecom.repository.database.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmailVerificationToken(String token);

    Optional<UserEntity> findByPasswordResetToken(String token);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query(value = "SELECT u FROM UserEntity u LEFT JOIN FETCH u.userInfo",
           countQuery = "SELECT count(u) FROM UserEntity u")
    Page<UserEntity> findAll(Pageable pageable);

}