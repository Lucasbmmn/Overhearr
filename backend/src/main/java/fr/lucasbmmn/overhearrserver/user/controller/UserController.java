package fr.lucasbmmn.overhearrserver.user.controller;

import fr.lucasbmmn.overhearrserver.common.dto.PageResponse;
import fr.lucasbmmn.overhearrserver.user.dto.*;
import fr.lucasbmmn.overhearrserver.user.mapper.UserMapper;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for managing {@link User} resources.
 * <p>
 * All endpoints are secured.
 * Admin-only operations (listing, creating, admin-updating, and deleting users) require the
 * {@code ADMIN} role.
 * Self-service endpoints (profile update, password change) are available to any authenticated user.
 * </p>
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Retrieves a paginated list of all users, sorted by username.
     *
     * @param pageNumber The zero-based page index (default {@code 0}).
     * @param pageSize   The number of users per page (default {@code 10}, min {@code 100},
     *                   max {@code 100}).
     * @param order      The sort direction for usernames (default {@code ASC}).
     * @return A paginated {@link PageResponse} of {@link UserResponse} objects.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize,
            @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        Page<User> userPage = userService.findAllUsers(pageNumber, pageSize, order);
        Page<UserResponse> responsePage = userPage.map(userMapper::toResponse);
        return ResponseEntity.ok(PageResponse.from(responsePage));
    }

    /**
     * Creates a new user account.
     *
     * @param request The validated user creation payload.
     * @return The created {@link UserResponse} with HTTP 201 Created.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        User createdUser = userService.createUser(request);
        return new ResponseEntity<>(userMapper.toResponse(createdUser), HttpStatus.CREATED);
    }

    /**
     * Updates the currently authenticated user's own profile (username and email).
     *
     * @param request        The validated update payload.
     * @param requestingUser The authenticated user extracted from the security context.
     * @return The updated {@link UserResponse}.
     */
    @PutMapping("me")
    public ResponseEntity<UserResponse> updateUserMe(@Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal(expression = "user") User requestingUser) {
        return ResponseEntity.ok(
                userMapper.toResponse(userService.updateUser(requestingUser.getId(), request)));
    }

    /**
     * Updates any user's profile as an administrator (username, email, and role).
     *
     * @param id      The UUID of the user to update.
     * @param request The validated admin update payload.
     * @return The updated {@link UserResponse}.
     */
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserById(@PathVariable UUID id,
            @Valid @RequestBody AdminUserUpdateRequest request) {
        return ResponseEntity.ok(
                userMapper.toResponse(userService.updateUser(id, request)));
    }

    /**
     * Changes the currently authenticated user's password.
     *
     * @param request        The validated password update payload containing the old and new
     *                       passwords.
     * @param requestingUser The authenticated user extracted from the security context.
     * @return HTTP 204 No Content on success.
     */
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest request,
            @AuthenticationPrincipal(expression = "user") User requestingUser) {
        userService.updatePassword(requestingUser.getId(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a user by their ID. An administrator cannot delete their own account.
     *
     * @param id             The UUID of the user to delete.
     * @param requestingUser The authenticated administrator extracted from the security context.
     * @return HTTP 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id,
            @AuthenticationPrincipal(expression = "user") User requestingUser) {
        userService.deleteUser(id, requestingUser.getId());
        return ResponseEntity.noContent().build();
    }
}
