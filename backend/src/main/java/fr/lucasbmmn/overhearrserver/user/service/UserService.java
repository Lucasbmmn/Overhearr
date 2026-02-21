package fr.lucasbmmn.overhearrserver.user.service;

import fr.lucasbmmn.overhearrserver.user.dto.AdminUserUpdateRequest;
import fr.lucasbmmn.overhearrserver.user.dto.PasswordUpdateRequest;
import fr.lucasbmmn.overhearrserver.user.dto.UserCreationRequest;
import fr.lucasbmmn.overhearrserver.user.dto.UserUpdateRequest;
import fr.lucasbmmn.overhearrserver.user.model.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
     * Retrieves a paginated list of all users.
     *
     * @param pageNumber The zero-based page index.
     * @param pageSize   The number of users per page.
     * @param direction  The sort direction for the username field.
     * @return A {@link Page} of users.
     */
    Page<User> findAllUsers(int pageNumber, int pageSize, Sort.Direction direction);

    /**
     * Finds a user by their unique identifier.
     *
     * @param id The UUID of the user.
     * @return An {@link Optional} containing the user if found, or empty otherwise.
     */
    Optional<User> findById(UUID id);

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

    /**
     * Finds a user by their email address.
     *
     * @param email The email to search for.
     * @return An {@link Optional} containing the user if found, or empty otherwise.
     */
    Optional<User> findByEmail(@NonNull String email);

    /**
     * Updates a user's profile as an administrator (username, email, role).
     *
     * @param userId  The UUID of the user to update.
     * @param request The admin update payload.
     * @return The updated {@link User} entity.
     */
    User updateUser(@NonNull UUID userId, @NonNull AdminUserUpdateRequest request);

    /**
     * Updates a user's own profile (username, email).
     *
     * @param userId  The UUID of the user to update.
     * @param request The self-service update payload.
     * @return The updated {@link User} entity.
     */
    User updateUser(@NonNull UUID userId, @NonNull UserUpdateRequest request);

    /**
     * Updates a user's password after verifying the current one.
     *
     * @param id      The UUID of the user.
     * @param request The password update payload.
     */
    void updatePassword(@NonNull UUID id, @NonNull PasswordUpdateRequest request);

    /**
     * Deletes a user by ID, preventing self-deletion.
     *
     * @param targetUserId     The UUID of the user to delete.
     * @param requestingUserId The UUID of the administrator performing the deletion.
     */
    void deleteUser(@NonNull UUID targetUserId, @NonNull UUID requestingUserId);
}
