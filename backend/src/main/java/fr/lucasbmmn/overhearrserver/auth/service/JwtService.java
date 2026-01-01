package fr.lucasbmmn.overhearrserver.auth.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for handling JSON Web Tokens (JWTs).
 * <p>
 * Defines operations for generating, parsing, and validating tokens used for authentication.
 * </p>
 */
public interface JwtService {
    /**
     * Extracts the username (subject) from the provided JWT token.
     *
     * @param token The JWT string.
     * @return The username stored in the token's subject claim.
     */
    String extractUsername(String token);

    /**
     * Generates a new access token for a given username.
     *
     * @param username The username to include in the token.
     * @return A JWT access token string.
     */
    String generateAccessToken(String username);

    /**
     * Validates an access token against user details.
     * Checks for matching username, token expiration, and correct token type.
     *
     * @param token       The access token to validate.
     * @param userDetails The user details to validate against.
     * @return {@code true} if the token is valid, otherwise {@code false}.
     */
    boolean validateAccessToken(String token, UserDetails userDetails);
}
