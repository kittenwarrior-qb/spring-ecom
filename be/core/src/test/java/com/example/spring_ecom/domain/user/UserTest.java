package com.example.spring_ecom.domain.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User makeUser(Long id, String username, String email, Long roleId, boolean emailVerified, boolean active) {
        LocalDateTime now = LocalDateTime.now();
        return new User(id, username, email, "hashedPassword", roleId,
                emailVerified, null, null, null, null, null, active, now, now, null);
    }

    @Test
    void shouldCreateUserWithAllFields() {
        // When
        User user = makeUser(1L, "testuser", "test@example.com", 3L, true, true);

        // Then
        assertNotNull(user);
        assertEquals(1L, user.id());
        assertEquals("testuser", user.username());
        assertEquals("test@example.com", user.email());
        assertEquals(3L, user.roleId());
        assertTrue(user.isEmailVerified());
        assertTrue(user.isActive());
    }

    @Test
    void shouldCreateUserWithMinimalFields() {
        // When
        User user = makeUser(1L, "testuser", "test@example.com", 3L, false, true);

        // Then
        assertNotNull(user);
        assertEquals(1L, user.id());
        assertFalse(user.isEmailVerified());
        assertTrue(user.isActive());
    }

    @Test
    void shouldCompareUsersWithSameData() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User(1L, "testuser", "test@example.com", "password",
                3L, false, null, null, null, null, null, true, now, now, null);
        User user2 = new User(1L, "testuser", "test@example.com", "password",
                3L, false, null, null, null, null, null, true, now, now, null);

        // Then
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void shouldHaveAdminRole() {
        // Given & When
        User adminUser = makeUser(1L, "admin", "admin@example.com", 1L, true, true);

        // Then
        assertEquals(1L, adminUser.roleId()); // 1 = ADMIN
    }

    @Test
    void shouldHaveUserRole() {
        // Given & When
        User regularUser = makeUser(2L, "user", "user@example.com", 3L, false, true);

        // Then
        assertEquals(3L, regularUser.roleId()); // 3 = USER
    }
}
