package fr.lucasbmmn.overhearrserver.auth.model;

import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    private User createUser(UserRole role) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPasswordHash("hashedPassword123");
        user.setRole(role);
        return user;
    }

    @Test
    void getAuthorities_UserRole_ReturnsRoleUser() {
        User user = createUser(UserRole.USER);
        CustomUserDetails details = new CustomUserDetails(user);

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());
    }

    @Test
    void getAuthorities_AdminRole_ReturnsRoleAdmin() {
        User user = createUser(UserRole.ADMIN);
        CustomUserDetails details = new CustomUserDetails(user);

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertEquals(1, authorities.size());
        assertEquals("ROLE_ADMIN", authorities.iterator().next().getAuthority());
    }

    @Test
    void getPassword_ReturnsPasswordHash() {
        User user = createUser(UserRole.USER);
        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals("hashedPassword123", details.getPassword());
    }

    @Test
    void getUsername_ReturnsUserIdAsString() {
        User user = createUser(UserRole.USER);
        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals(user.getId().toString(), details.getUsername());
    }
}
