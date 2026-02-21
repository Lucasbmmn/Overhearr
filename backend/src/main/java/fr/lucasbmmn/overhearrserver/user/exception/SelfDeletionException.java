package fr.lucasbmmn.overhearrserver.user.exception;

import fr.lucasbmmn.overhearrserver.exception.ConflictException;

/**
 * Thrown when an administrator attempts to delete their own account.
 */
public class SelfDeletionException extends ConflictException {
    /**
     * @param message A description of the conflict.
     */
    public SelfDeletionException(String message) {
        super(message);
    }
}
