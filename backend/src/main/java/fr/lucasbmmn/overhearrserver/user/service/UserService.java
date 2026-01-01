package fr.lucasbmmn.overhearrserver.user.service;

import fr.lucasbmmn.overhearrserver.user.dto.UserCreationRequest;
import fr.lucasbmmn.overhearrserver.user.model.User;
import lombok.NonNull;

import java.util.Optional;

/**
 * Service interface for managing {@link User} entities.
 */
public interface UserService {
    /**
     * Creates a new user in the system.
     *
     * @param request The data required to create the user.
     * @return The created {@link User} entity.
     * @throws IllegalArgumentException if a user with the same username or email already exists.
     */
    User createUser(@NonNull UserCreationRequest request);

    /**
     * Finds a user by their username or email address.
     *
     * @param identifier The username or email to search for.
     * @return An {@link Optional} containing the user if found, or empty otherwise.
     */
    Optional<User> findByUsernameOrEmail(@NonNull String identifier);

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return An {@link Optional} containing the user if found, or empty otherwise.
     */
    Optional<User> findByUsername(@NonNull String username);
}
