package com.example.spring_ecom.repository.database.user;

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

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.isActive = true")
    Optional<UserEntity> findActiveByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.isActive = true")
    Optional<UserEntity> findActiveByUsername(@Param("username") String username);

}
