package fr.lucasbmmn.overhearrserver.auth.controller;

import fr.lucasbmmn.overhearrserver.auth.constant.AuthConstants;
import fr.lucasbmmn.overhearrserver.auth.service.AuthService;
import fr.lucasbmmn.overhearrserver.auth.dto.LoginRequest;
import fr.lucasbmmn.overhearrserver.auth.dto.AuthResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * REST Controller for managing user authentication.
 * <p>
 * Provides endpoints for logging in (JWT generation) and logging out (cookie clearance).
 * </p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${overhearr.token.jwt.expiration:730d}")
    private Duration tokenExpiration;

    /**
     * Authenticates a user and establishes a session via an HTTP-only cookie.
     *
     * @param request      The login request containing identifier and password.
     * @param httpResponse The HTTP response to which the authentication cookie will be added.
     * @return A {@link ResponseEntity} containing the {@link AuthResponse} with the access token and user details.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request,
                                                  HttpServletResponse httpResponse) {
        AuthResponse authResponse = authService.login(request.identifier(), request.password());
        httpResponse.setHeader(
                "Set-Cookie",
                createTokenCookie(authResponse.accessToken(), tokenExpiration)
        );
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Logs out the current user by clearing the authentication cookie.
     *
     * @param httpResponse The HTTP response used to invalidate the cookie.
     */
    @PostMapping("/logout")
    public void logout(HttpServletResponse httpResponse) {
        httpResponse.addHeader("Set-Cookie", createTokenCookie(null, Duration.ZERO));
    }

    /**
     * Helper method to create a secure HTTP-only cookie for the JWT.
     *
     * @param token      The JWT token string (or null for deletion).
     * @param expiration The duration until the cookie expires.
     * @return The string representation of the Set-Cookie header.
     */
    private static String createTokenCookie(String token, Duration expiration) {
        return ResponseCookie.from(AuthConstants.ACCESS_TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .path("/")
                .maxAge(expiration)
                .sameSite("Strict")
                .build()
                .toString();
    }
}
