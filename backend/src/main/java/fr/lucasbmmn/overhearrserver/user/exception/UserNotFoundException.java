package fr.lucasbmmn.overhearrserver.user.exception;

import fr.lucasbmmn.overhearrserver.exception.NotFoundException;

import java.util.UUID;

/**
 * Thrown when no {@link fr.lucasbmmn.overhearrserver.user.model.User} exists for the given ID.
 */
public class UserNotFoundException extends NotFoundException {
    /**
     * @param id The UUID that could not be resolved to a user.
     */
    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }
}
