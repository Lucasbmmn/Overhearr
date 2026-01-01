package fr.lucasbmmn.overhearrserver.auth.service;

import fr.lucasbmmn.overhearrserver.auth.dto.AuthResponse;
import lombok.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {
    /**
     * Authenticates a user based on their identifier (username or email) and password.
     *
     * @param identifier The user's username or email.
     * @param password   The user's raw password.
     * @return An {@link AuthResponse} containing the generated JWT and user details.
     * @throws UsernameNotFoundException if no user is found with the given identifier.
     * @throws BadCredentialsException   if the provided password does not match the stored hash.
     */
    AuthResponse login(@NonNull String identifier, @NonNull String password);
}
