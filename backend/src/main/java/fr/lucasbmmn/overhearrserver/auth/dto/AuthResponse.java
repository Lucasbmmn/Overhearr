package fr.lucasbmmn.overhearrserver.auth.dto;

import fr.lucasbmmn.overhearrserver.user.dto.UserResponse;

/**
 * DTO for the successful authentication response.
 *
 * @param accessToken The JSON Web Token (JWT) used for accessing protected resources.
 * @param user        The details of the authenticated user.
 */
public record AuthResponse(
        String accessToken,
        UserResponse user
) {}