package fr.lucasbmmn.overhearrserver.auth.dto;

/**
 * DTO representing a user's login credentials.
 * Used to capture the identifier and password from the login request body.
 *
 * @param identifier The unique identifier for the user (username or email).
 * @param password   The user's raw password.
 */
public record LoginRequest(
        String identifier,
        String password
) {}