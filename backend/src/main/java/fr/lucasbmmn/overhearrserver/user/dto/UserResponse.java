package fr.lucasbmmn.overhearrserver.user.dto;

import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO representing a user entity exposed to API consumers.
 *
 * @param id        The unique identifier for the user.
 *                  <p>
 *                  Generated automatically using UUID v4.
 * @param username  The unique username for the user.
 *                  <p>
 *                  Constraints:
 *                  <ul>
 *                  <li>Must be unique across the system.</li>
 *                  <li>Maximum length: 50 characters.</li>
 *                  <li>Cannot be null.</li>
 *                  </ul>
 * @param email     The user's email address.
 *                  <p>
 *                  Maximum length: 255 characters.
 * @param role      The role assigned to the user.
 *                  <p>
 *                  Constraints:
 *                  <ul>
 *                  <li>Cannot be null.</li>
 *                  </ul>
 * @param createdAt The timestamp when the user was created.
 * @param updatedAt The timestamp when the user was last updated.
 */
public record UserResponse(
        @NonNull UUID id,

        @NonNull String username,

        String email,

        @NonNull UserRole role,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {
    /**
     * Convenience constructor that creates a response without an email address.
     *
     * @param id        The user's unique identifier.
     * @param username  The user's username.
     * @param role      The user's role.
     * @param createdAt The creation timestamp.
     * @param updatedAt The last-updated timestamp.
     */
    public UserResponse(@NonNull UUID id, @NonNull String username, @NonNull UserRole role,
            @NonNull Instant createdAt, @NonNull Instant updatedAt) {
        this(id, username, null, role, createdAt, updatedAt);
    }
}