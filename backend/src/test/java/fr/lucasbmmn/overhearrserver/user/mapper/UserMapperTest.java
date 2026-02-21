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
        UserCreationRequest request = new UserCreationRequest(
                "testuser",
                "test@example.com",
                "Password",
                UserRole.ADMIN
        );

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

        assertEquals(user.getId(), response.id());
        assertEquals(user.getUsername(), response.username());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getRole(), response.role());
        assertEquals(user.getCreatedAt(), response.createdAt());
        assertEquals(user.getUpdatedAt(), response.updatedAt());
    }
}