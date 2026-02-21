package fr.lucasbmmn.overhearrserver.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Global exception handler that translates exceptions into structured {@link ExceptionResponse}
 * payloads with appropriate HTTP status codes.
 * <p>
 * All handlers produce a uniform JSON error body containing the status code, error label,
 * descriptive message, timestamp, and request path.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles application-level bad-request exceptions.
     *
     * @param e       The bad-request exception.
     * @param request The current HTTP request.
     * @return A 400 Bad Request response.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequest(BadRequestException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(this.createExceptionResponse(e, request, status), status);
    }

    /**
     * Handles bean-validation failures, aggregating field-error messages into a single response.
     *
     * @param e       The validation exception containing field errors.
     * @param request The current HTTP request.
     * @return A 400 Bad Request response with concatenated validation messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationErrors(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ExceptionResponse response = ExceptionResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Validation failed: " + errorMessage)
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, status);
    }

    /**
     * Handles type-mismatch errors on request parameters (e.g. invalid UUID format).
     *
     * @param e       The type-mismatch exception.
     * @param request The current HTTP request.
     * @return A 400 Bad Request response.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(ExceptionResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Invalid parameter: " + e.getName())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build(), status);
    }

    /**
     * Handles Spring Security authentication failures.
     *
     * @param e       The authentication exception.
     * @param request The current HTTP request.
     * @return A 401 Unauthorized response.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException e,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(this.createExceptionResponse(e, request, status), status);
    }

    /**
     * Handles access-denied errors when an authenticated user lacks the required authority.
     *
     * @param e       The access-denied exception.
     * @param request The current HTTP request.
     * @return A 403 Forbidden response.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(this.createExceptionResponse(e, request, status), status);
    }

    /**
     * Handles application-level not-found exceptions.
     *
     * @param e       The not-found exception.
     * @param request The current HTTP request.
     * @return A 404 Not Found response.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(NotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(this.createExceptionResponse(e, request, status), status);
    }

    /**
     * Handles unsupported HTTP method errors.
     *
     * @param e       The method-not-supported exception.
     * @param request The current HTTP request.
     * @return A 406 Not Acceptable response.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(HttpRequestMethodNotSupportedException e,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
        return new ResponseEntity<>(this.createExceptionResponse(e, request, status), status);
    }

    /**
     * Handles application-level conflict exceptions.
     *
     * @param e       The conflict exception.
     * @param request The current HTTP request.
     * @return A 409 Conflict response.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionResponse> handleConflict(ConflictException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(this.createExceptionResponse(e, request, status), status);
    }

    /**
     * Catch-all handler for any unhandled exceptions.
     * Logs the full stack trace and returns a generic 500 Internal Server Error response.
     *
     * @param e       The unexpected exception.
     * @param request The current HTTP request.
     * @return A 500 Internal Server Error response with a generic message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGlobalException(Exception e, HttpServletRequest request) {
        log.error("An unexpected error occurred on path: {}", request.getRequestURI(), e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionResponse response = ExceptionResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("An unexpected error occurred")
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, status);
    }

    /**
     * Builds an {@link ExceptionResponse} from the given exception, request, and status.
     *
     * @param e       The exception that occurred.
     * @param request The current HTTP request.
     * @param status  The HTTP status to include in the response body.
     * @return A populated {@link ExceptionResponse}.
     */
    private ExceptionResponse createExceptionResponse(Exception e, HttpServletRequest request,
            HttpStatus status) {
        return ExceptionResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(e.getMessage())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }
}
