package fr.lucasbmmn.overhearrserver.user.exception;

import fr.lucasbmmn.overhearrserver.exception.BadRequestException;

/**
 * Thrown when a provided password does not match the expected value
 * (e.g. during a password change).
 */
public class InvalidPasswordException extends BadRequestException {
    /**
     * @param message A description of why the password was rejected.
     */
    public InvalidPasswordException(String message) {
        super(message);
    }
}
