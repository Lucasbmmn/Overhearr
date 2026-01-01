package fr.lucasbmmn.overhearrserver.user.dto;

import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserResponse {
    /**
     * The unique identifier for the user.
     * <p>
     * Generated automatically using UUID v4.
     */
    @NonNull
    private UUID id;

    /**
     * The unique username for the user.
     * <p>
     * Constraints:
     * <ul>
     * <li>Must be unique across the system.</li>
     * <li>Maximum length: 50 characters.</li>
     * <li>Cannot be null.</li>
     * </ul>
     */
    @NonNull
    private String username;

    /**
     * The user's email address.
     * <p>
     * Maximum length: 255 characters.
     */
    private String email;

    /**
     * The role assigned to the user.
     * <p>
     * Constraints:
     * <ul>
     * <li>Cannot be null.</li>
     * </ul>
     */
    @NonNull
    private UserRole role;

    /**
     * The timestamp when the user was created.
     */
    @NonNull
    private Instant createdAt;

    /**
     * The timestamp when the user was last updated.
     */
    @NonNull
    private Instant updatedAt;
}
