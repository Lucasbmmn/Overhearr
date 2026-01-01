package fr.lucasbmmn.overhearrserver.auth.dto;

import lombok.Data;

/**
 * DTO representing a user's login credentials.
 * Used to capture the identifier and password from the login request body.
 */
@Data
public class LoginRequest {
    /**
     * The unique identifier for the user (username or email).
     */
    private String identifier;

    /**
     * The user's raw password.
     */
    private String password;
}