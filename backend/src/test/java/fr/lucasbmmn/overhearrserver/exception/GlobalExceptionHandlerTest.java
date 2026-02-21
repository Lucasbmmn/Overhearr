package fr.lucasbmmn.overhearrserver.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test/path");
    }

    @Test
    void handleBadRequest_Returns400() {
        BadRequestException ex = new BadRequestException("Bad request message") {
        };

        ResponseEntity<ExceptionResponse> response = handler.handleBadRequest(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad request message", response.getBody().getMessage());
        assertEquals("/test/path", response.getBody().getPath());
    }

    @Test
    void handleValidationErrors_Returns400WithJoinedMessages() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("obj", "field1", "must not be blank");
        FieldError fieldError2 = new FieldError("obj", "field2", "must be valid");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ExceptionResponse> response = handler.handleValidationErrors(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("must not be blank"));
        assertTrue(response.getBody().getMessage().contains("must be valid"));
    }

    @Test
    void handleTypeMismatch_Returns400WithParamName() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "badValue", Integer.class, "pageSize", null,
                new NumberFormatException("For input string: \"badValue\""));

        ResponseEntity<ExceptionResponse> response = handler.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("pageSize"));
    }

    @Test
    void handleAuthenticationException_Returns401() {
        BadCredentialsException ex = new BadCredentialsException("Invalid credentials");

        ResponseEntity<ExceptionResponse> response = handler.handleAuthenticationException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }

    @Test
    void handleAccessDeniedException_Returns403() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<ExceptionResponse> response = handler.handleAccessDeniedException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Access denied", response.getBody().getMessage());
    }

    @Test
    void handleNotFound_Returns404() {
        NotFoundException ex = new NotFoundException("Resource not found") {
        };

        ResponseEntity<ExceptionResponse> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Resource not found", response.getBody().getMessage());
    }

    @Test
    void handleMethodNotSupported_Returns406() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("DELETE");

        ResponseEntity<ExceptionResponse> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(406, response.getBody().getStatus());
    }

    @Test
    void handleConflict_Returns409() {
        ConflictException ex = new ConflictException("Conflict occurred") {
        };

        ResponseEntity<ExceptionResponse> response = handler.handleConflict(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict occurred", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException_Returns500WithGenericMessage() {
        Exception ex = new RuntimeException("Something went wrong");

        ResponseEntity<ExceptionResponse> response = handler.handleGlobalException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
}
