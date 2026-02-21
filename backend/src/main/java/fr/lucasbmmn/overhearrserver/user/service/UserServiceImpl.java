package fr.lucasbmmn.overhearrserver.user.service;

import fr.lucasbmmn.overhearrserver.user.dto.AdminUserUpdateRequest;
import fr.lucasbmmn.overhearrserver.user.exception.InvalidPasswordException;
import fr.lucasbmmn.overhearrserver.user.exception.SelfDeletionException;
import fr.lucasbmmn.overhearrserver.user.exception.UserAlreadyExistsException;
import fr.lucasbmmn.overhearrserver.user.exception.UserNotFoundException;
import fr.lucasbmmn.overhearrserver.user.dto.PasswordUpdateRequest;
import fr.lucasbmmn.overhearrserver.user.dto.UserCreationRequest;
import fr.lucasbmmn.overhearrserver.user.dto.UserUpdateRequest;
import fr.lucasbmmn.overhearrserver.user.mapper.UserMapper;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import fr.lucasbmmn.overhearrserver.user.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link UserService}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(@NonNull UserCreationRequest request) {
        if (userRepository.findByUsernameOrEmail(request.username(),
                request.email() != null ? request.email() : request.username()).isPresent()) {
            throw new UserAlreadyExistsException("User with this username or email already exists");
        }

        User user = this.userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating user", e);
            throw new UserAlreadyExistsException("User with this username or email already exists");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllUsers(int pageNumber, int pageSize, Sort.Direction direction) {
        Sort sort = Sort.by(direction, "username");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(@NonNull String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(@NonNull String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(@NonNull String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateUser(@NonNull UUID userId, @NonNull AdminUserUpdateRequest request) {
        User user = this.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return updateUser(user, request.username(), request.email(), request.role());
    }

    @Override
    public User updateUser(@NonNull UUID userId, @NonNull UserUpdateRequest request) {
        User user = this.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return updateUser(user, request.username(), request.email(), user.getRole());
    }

    /**
     * Internal helper that applies username, email, and role changes to an existing user,
     * checking for uniqueness violations before persisting.
     *
     * @param user        The user entity to update.
     * @param newUsername The new username to set.
     * @param newEmail    The new email to set (may be {@code null}).
     * @param newRole     The new role to set.
     * @return The persisted, updated {@link User} entity.
     * @throws UserAlreadyExistsException if the new username or email is already
     *                                    taken by another user.
     */
    private User updateUser(@NonNull User user, @NonNull String newUsername, String newEmail,
            @NonNull UserRole newRole) {
        if (!newUsername.equals(user.getUsername())) {
            this.findByUsername(newUsername)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(user.getId())) {
                            throw new UserAlreadyExistsException("Username '" + newUsername + "' is already taken");
                        }
                    });
            user.setUsername(newUsername);
        }

        if (!Objects.equals(newEmail, user.getEmail())) {
            if (newEmail != null) {
                this.findByEmail(newEmail)
                        .ifPresent(existing -> {
                            if (!existing.getId().equals(user.getId())) {
                                throw new UserAlreadyExistsException("Email '" + newEmail + "' is already taken");
                            }
                        });
            }
            user.setEmail(newEmail);
        }

        user.setRole(newRole);

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("User with this username or email already exists");
        }
    }

    @Override
    public void updatePassword(@NonNull UUID id, @NonNull PasswordUpdateRequest request) {
        User user = this.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidPasswordException("Current password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);
    }

    @Override
    public void deleteUser(@NonNull UUID targetUserId, @NonNull UUID requestingUserId) {
        if (targetUserId.equals(requestingUserId)) {
            throw new SelfDeletionException("You cannot delete your own account.");
        }

        if (!userRepository.existsById(targetUserId)) {
            throw new UserNotFoundException(targetUserId);
        }
        userRepository.deleteById(targetUserId);
    }
}