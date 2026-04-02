package fr.lucasbmmn.overhearrserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Abstract base exception for all errors that should result in an HTTP 502 Bad
 * Gateway response.
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public abstract class BadGatewayException extends RuntimeException {
    /**
     * @param message A human-readable description of the gateway error.
     */
    public BadGatewayException(String message) {
        super(message);
    }

    /**
     * @param message A human-readable description of the gateway error.
     * @param cause   The underlying cause of the exception.
     */
    public BadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
