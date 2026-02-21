package fr.lucasbmmn.overhearrserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Abstract base exception for all errors that should result in an HTTP 404 Not Found response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public abstract class NotFoundException extends RuntimeException {
    /**
     * @param message A human-readable description of the missing resource.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
