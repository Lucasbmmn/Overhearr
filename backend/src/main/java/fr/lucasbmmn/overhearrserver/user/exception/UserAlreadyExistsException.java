package fr.lucasbmmn.overhearrserver.user.exception;

import fr.lucasbmmn.overhearrserver.exception.ConflictException;

/**
 * Thrown when a user creation or update would violate a uniqueness constraint (username or email).
 */
public class UserAlreadyExistsException extends ConflictException {
    /**
     * @param message A description of the conflict.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
