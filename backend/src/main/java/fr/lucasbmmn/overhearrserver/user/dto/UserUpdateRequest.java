package fr.lucasbmmn.overhearrserver.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for a user updating their own profile (username and email).
 *
 * @param username The unique username for the user.
 *                 <p>
 *                 Constraints:
 *                 <ul>
 *                 <li>Must be unique across the system.</li>
 *                 <li>Maximum length: 50 characters.</li>
 *                 <li>Cannot be null.</li>
 *                 </ul>
 * @param email    The user's email address.
 *                 <p>
 *                 Maximum length: 255 characters.
 */
public record UserUpdateRequest(
                @NotBlank(message = "Username is required")
                @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
                @Pattern(
                        regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9]|[._](?![._]))*[a-zA-Z0-9]$",
                        message = "Username must start/end with alphanumeric and contain no consecutive dots/underscores"
                )
                String username,

                @Email
                String email) {
}
