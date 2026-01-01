package fr.lucasbmmn.overhearrserver.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a registered user in the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    /**
     * The unique identifier for the user.
     * <p>
     * Generated automatically using UUID v4.
     */
    @NonNull
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * The user's email address.
     * <p>
     * Maximum length: 255 characters.
     */
    @Column
    private String email;

    /**
     * The hashed password for the user.
     * <p>
     * Constraints:
     * <ul>
     * <li>Cannot be null.</li>
     * <li>Maximum length: 255 characters.</li>
     * </ul>
     */
    @NonNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * The role assigned to the user.
     * <p>
     * Constraints:
     * <ul>
     * <li>Cannot be null.</li>
     * </ul>
     */
    @NonNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    /**
     * The timestamp when the user was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    /**
     * The timestamp when the user was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}