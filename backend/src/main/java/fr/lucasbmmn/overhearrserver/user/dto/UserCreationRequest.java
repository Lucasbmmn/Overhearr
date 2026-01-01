package fr.lucasbmmn.overhearrserver.user.dto;

import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserCreationRequest {
    /**
     * The desired username.
     * <p>
     * Constraints:
     * <ul>
     * <li>Required (Not blank)</li>
     * <li>Length: 3 to 20 characters</li>
     * <li>Must contain only alphanumeric character, dots or underscores</li>
     * <li>Must start and end with an alphanumeric character</li>
     * <li>No consecutive dots or underscores</li>
     * </ul>
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 8 and 20 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9]|[._](?![._]))*[a-zA-Z0-9]$",
            message = "Username must start/end with alphanumeric and contain no consecutive dots/underscores"
    )
    private String username;

    /**
     * The user's email address.
     * Must be a valid email format.
     */
    @Email
    private String email;

    /**
     * The raw password for the new account.
     * <p>
     * Constraints:
     * <ul>
     * <li>Required</li>
     * <li>Minimum length: 8 characters</li>
     * <li>Must contain at least one uppercase letter</li>
     * <li>Must contain at least one lowercase letter</li>
     * <li>Must contain at least one digit</li>
     * <li>Must contain at least one special character (@$!%*?&)</li>
     * </ul>
     */
    @NotBlank(message = "Password is required")
    @Pattern.List({
            @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter"),
            @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter"),
            @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit"),
            @Pattern(regexp = ".*[@$!%*?&].*", message = "Password must contain at least one special character (@$!%*?&)")
    })
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    /**
     * The role to assign to the new user.
     * Cannot be null.
     */
    @NotNull
    private UserRole role;
}
