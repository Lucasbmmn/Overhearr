package fr.lucasbmmn.overhearrserver.user.dto;

import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import jakarta.validation.constraints.*;

/**
 * A DTO representing a request to create a new user.
 *
 * @param username The desired username.
 *                 <p>
 *                 Constraints:
 *                 <ul>
 *                 <li>Required (Not blank)</li>
 *                 <li>Length: 3 to 20 characters</li>
 *                 <li>Must contain only alphanumeric character, dots or
 *                 underscores</li>
 *                 <li>Must start and end with an alphanumeric character</li>
 *                 <li>No consecutive dots or underscores</li>
 *                 </ul>
 * @param email    The user's email address. Must be a valid email format.
 * @param password The raw password for the new account.
 *                 <p>
 *                 Constraints:
 *                 <ul>
 *                 <li>Required</li>
 *                 <li>Minimum length: 8 characters</li>
 *                 <li>Must contain at least one uppercase letter</li>
 *                 <li>Must contain at least one lowercase letter</li>
 *                 <li>Must contain at least one digit</li>
 *                 <li>Must contain at least one special character (@$!%*?&)</li>
 *                 </ul>
 * @param role     The role to assign to the new user. Cannot be null.
 */
public record UserCreationRequest(
                @NotBlank(message = "Username is required")
                @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
                @Pattern(
                        regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9]|[._](?![._]))*[a-zA-Z0-9]$",
                        message = "Username must start/end with alphanumeric and contain no consecutive dots/underscores"
                )
                String username,

                @Email
                String email,

                @NotBlank(message = "Password is required")
                @Pattern.List({
                                @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter"),
                                @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter"),
                                @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit"),
                                @Pattern(regexp = ".*[\\W_].*", message = "Password must contain at least one special character (@$!%*?&)")
                })
                @Size(min = 8, message = "Password must be at least 8 characters long")
                String password,

                @NotNull
                UserRole role) {
        /**
         * Convenience constructor that creates a request without an email address.
         *
         * @param username The desired username.
         * @param password The raw password.
         * @param role     The role to assign.
         */
        public UserCreationRequest(String username, String password, UserRole role) {
                this(username, null, password, role);
        }
}