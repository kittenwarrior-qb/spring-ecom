package com.example.spring_ecom.domain.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithAllFields() {
        // Given
        Long id = 1L;
        String username = "testuser";
        String email = "test@example.com";
        String password = "hashedPassword";
        String firstName = "John";
        String lastName = "Doe";
        String phoneNumber = "0123456789";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        String avatarUrl = "https://example.com/avatar.jpg";
        UserRole role = UserRole.USER;
        Boolean isEmailVerified = true;
        Boolean isActive = true;
        LocalDateTime now = LocalDateTime.now();

        // When
        User user = new User(
            id, username, email, password, firstName, lastName,
            phoneNumber, dateOfBirth, avatarUrl, role, isEmailVerified,
            null, null, null, null, now, isActive, now, now, null
        );

        // Then
        assertNotNull(user);
        assertEquals(id, user.id());
        assertEquals(username, user.username());
        assertEquals(email, user.email());
        assertEquals(password, user.password());
        assertEquals(firstName, user.firstName());
        assertEquals(lastName, user.lastName());
        assertEquals(phoneNumber, user.phoneNumber());
        assertEquals(dateOfBirth, user.dateOfBirth());
        assertEquals(avatarUrl, user.avatarUrl());
        assertEquals(role, user.role());
        assertEquals(isEmailVerified, user.isEmailVerified());
        assertEquals(isActive, user.isActive());
    }

    @Test
    void shouldCreateUserWithMinimalFields() {
        // Given
        Long id = 1L;
        String username = "testuser";
        String email = "test@example.com";
        String password = "hashedPassword";
        UserRole role = UserRole.USER;
        LocalDateTime now = LocalDateTime.now();

        // When
        User user = new User(
            id, username, email, password, null, null,
            null, null, null, role, false,
            null, null, null, null, null, true, now, now, null
        );

        // Then
        assertNotNull(user);
        assertEquals(id, user.id());
        assertEquals(username, user.username());
        assertEquals(email, user.email());
        assertNull(user.firstName());
        assertNull(user.lastName());
        assertFalse(user.isEmailVerified());
        assertTrue(user.isActive());
    }

    @Test
    void shouldCompareUsersWithSameData() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User(
            1L, "testuser", "test@example.com", "password",
            "John", "Doe", null, null, null, UserRole.USER,
            false, null, null, null, null, null, true, now, now, null
        );
        User user2 = new User(
            1L, "testuser", "test@example.com", "password",
            "John", "Doe", null, null, null, UserRole.USER,
            false, null, null, null, null, null, true, now, now, null
        );

        // Then
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void shouldHaveAdminRole() {
        // Given & When
        User adminUser = new User(
            1L, "admin", "admin@example.com", "password",
            null, null, null, null, null, UserRole.ADMIN,
            true, null, null, null, null, null, true,
            LocalDateTime.now(), LocalDateTime.now(), null
        );

        // Then
        assertEquals(UserRole.ADMIN, adminUser.role());
    }

    @Test
    void shouldHaveUserRole() {
        // Given & When
        User regularUser = new User(
            1L, "user", "user@example.com", "password",
            null, null, null, null, null, UserRole.USER,
            false, null, null, null, null, null, true,
            LocalDateTime.now(), LocalDateTime.now(), null
        );

        // Then
        assertEquals(UserRole.USER, regularUser.role());
    }
}
