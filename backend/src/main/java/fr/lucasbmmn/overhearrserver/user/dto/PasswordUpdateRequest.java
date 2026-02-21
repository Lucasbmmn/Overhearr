package fr.lucasbmmn.overhearrserver.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for changing a user's password.
 * <p>
 * The current password must be verified before the new password is accepted.
 * </p>
 *
 * @param password    The current password.
 * @param newPassword The raw password for the new account.
 *                    <p>
 *                    Constraints:
 *                    <ul>
 *                    <li>Required</li>
 *                    <li>Minimum length: 8 characters</li>
 *                    <li>Must contain at least one uppercase letter</li>
 *                    <li>Must contain at least one lowercase letter</li>
 *                    <li>Must contain at least one digit</li>
 *                    <li>Must contain at least one special character
 *                    (@$!%*?&)</li>
 *                    </ul>
 */
public record PasswordUpdateRequest(
                String password,

                @NotBlank(message = "Password is required")
                @Pattern.List({
                                @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter"),
                                @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter"),
                                @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit"),
                                @Pattern(regexp = ".*[@$!%*?&].*", message = "Password must contain at least one special character (@$!%*?&)")
                })
                @Size(min = 8, message = "Password must be at least 8 characters long")
                String newPassword) {
}
