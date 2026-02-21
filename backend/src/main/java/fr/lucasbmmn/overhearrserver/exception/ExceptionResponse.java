package fr.lucasbmmn.overhearrserver.exception;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Standard error response body returned by the API when an exception occurs.
 * <p>
 * Provides a consistent structure for error details including the HTTP status, a short error
 * label, a human-readable message, the occurrence timestamp, and the request path that triggered
 * the error.
 * </p>
 */
@Data
@Builder
public class ExceptionResponse {
    /** The numeric HTTP status code (e.g. {@code 400}, {@code 404}). */
    private int status;
    /** The short reason phrase for the status (e.g. "Bad Request"). */
    private String error;
    /** A human-readable description of the error. */
    private String message;
    /** The point in time at which the error occurred. */
    private Instant timestamp;
    /** The URI path of the request that caused the error. */
    private String path;
}