package fr.lucasbmmn.overhearrserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Abstract base exception for all errors that should result in an HTTP 409 Conflict response.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public abstract class ConflictException extends RuntimeException {
    /**
     * @param message A human-readable description of the conflict.
     */
    public ConflictException(String message) {
        super(message);
    }
}