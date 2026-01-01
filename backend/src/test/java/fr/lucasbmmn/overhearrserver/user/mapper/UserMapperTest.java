package fr.lucasbmmn.overhearrserver.user.mapper;

import fr.lucasbmmn.overhearrserver.user.dto.UserCreationRequest;
import fr.lucasbmmn.overhearrserver.user.dto.UserResponse;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toEntity_ShouldMapFieldsAndApplyConstants() {
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("Password");
        request.setRole(UserRole.ADMIN);

        User user = mapper.toEntity(request);

        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(UserRole.ADMIN, user.getRole());

        assertNull(user.getId());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
        assertNull(user.getPasswordHash());
    }

    @Test
    void toResponse_ShouldMapAllFields() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(UserRole.ADMIN);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setPasswordHash("hashedPassword");

        UserResponse response = mapper.toResponse(user);

        // Then
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getRole(), response.getRole());
        assertEquals(user.getCreatedAt(), response.getCreatedAt());
        assertEquals(user.getUpdatedAt(), response.getUpdatedAt());
    }
}