package fr.lucasbmmn.overhearrserver.auth.dto;

import fr.lucasbmmn.overhearrserver.user.dto.UserResponse;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for the successful authentication response.
 */
@Data
@Builder
public class AuthResponse {
    /**
     * The JSON Web Token (JWT) used for accessing protected resources.
     */
    private String accessToken;

    /**
     * The details of the authenticated user.
     */
    private UserResponse user;
}