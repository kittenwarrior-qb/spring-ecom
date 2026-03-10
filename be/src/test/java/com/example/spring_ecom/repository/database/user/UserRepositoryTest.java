package com.example.spring_ecom.repository.database.user;

import com.example.spring_ecom.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        testUser = UserEntity.builder()
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.USER)
            .isEmailVerified(false)
            .isActive(true)
            .build();
    }

    @Test
    void shouldSaveUser() {
        // When
        UserEntity savedUser = userRepository.save(testUser);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        userRepository.save(testUser);

        // When
        Optional<UserEntity> found = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void shouldNotFindUserByNonExistentEmail() {
        // When
        Optional<UserEntity> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindUserByUsername() {
        // Given
        userRepository.save(testUser);

        // When
        Optional<UserEntity> found = userRepository.findByUsername("testuser");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void shouldCheckIfEmailExists() {
        // Given
        userRepository.save(testUser);

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("other@example.com");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldCheckIfUsernameExists() {
        // Given
        userRepository.save(testUser);

        // When
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("otheruser");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldFindUserByEmailVerificationToken() {
        // Given
        testUser.setEmailVerificationToken("verification-token-123");
        userRepository.save(testUser);

        // When
        Optional<UserEntity> found = userRepository.findByEmailVerificationToken("verification-token-123");

        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void shouldFindUserByPasswordResetToken() {
        // Given
        testUser.setPasswordResetToken("reset-token-456");
        userRepository.save(testUser);

        // When
        Optional<UserEntity> found = userRepository.findByPasswordResetToken("reset-token-456");

        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void shouldFindOnlyActiveUsers() {
        // Given
        UserEntity activeUser = UserEntity.builder()
            .username("activeuser")
            .email("active@example.com")
            .password("password")
            .role(UserRole.USER)
            .isActive(true)
            .build();
        
        UserEntity inactiveUser = UserEntity.builder()
            .username("inactiveuser")
            .email("inactive@example.com")
            .password("password")
            .role(UserRole.USER)
            .isActive(false)
            .build();

        userRepository.save(activeUser);
        userRepository.save(inactiveUser);

        // When
        Page<UserEntity> activePage = userRepository.findAll(PageRequest.of(0, 10));

        // Then
        assertEquals(1, activePage.getTotalElements());
        assertTrue(activePage.getContent().get(0).getIsActive());
    }

    @Test
    void shouldUpdateUserTimestamps() throws InterruptedException {
        // Given
        UserEntity savedUser = userRepository.save(testUser);
        LocalDateTime createdAt = savedUser.getCreatedAt();
        
        Thread.sleep(100); 
        
        // When
        savedUser.setFirstName("Jane");
        UserEntity updatedUser = userRepository.save(savedUser);

        // Then
        assertEquals(createdAt, updatedUser.getCreatedAt());
        assertTrue(updatedUser.getUpdatedAt().isAfter(createdAt) || 
                   updatedUser.getUpdatedAt().isEqual(createdAt));
    }

    @Test
    void shouldSetDefaultValues() {
        // When
        UserEntity savedUser = userRepository.save(testUser);

        // Then
        assertEquals(UserRole.USER, savedUser.getRole());
        assertFalse(savedUser.getIsEmailVerified());
        assertTrue(savedUser.getIsActive());
    }
}
