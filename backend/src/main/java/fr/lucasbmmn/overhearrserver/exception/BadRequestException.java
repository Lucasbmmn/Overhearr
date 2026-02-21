package fr.lucasbmmn.overhearrserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Abstract base exception for all errors that should result in an HTTP 400 Bad Request response.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class BadRequestException extends RuntimeException {
    /**
     * @param message A human-readable description of the bad-request error.
     */
    public BadRequestException(String message) {
        super(message);
    }
}
