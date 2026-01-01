package fr.lucasbmmn.overhearrserver.user.repository;

import fr.lucasbmmn.overhearrserver.user.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link User} entity persistence.
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Retrieves a user by their specific username.
     *
     * @param username The username to search for.
     * @return An {@link Optional} containing the User if found.
     */
    Optional<User> findByUsername(@NonNull String username);

    /**
     * Retrieves a user who matches either the given username OR the given email.
     *
     * @param username The username to match.
     * @param email    The email to match.
     * @return An {@link Optional} containing the User if either field matches.
     */
    Optional<User> findByUsernameOrEmail(@NonNull String username, @NonNull String email);
}
