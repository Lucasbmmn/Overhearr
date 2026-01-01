package fr.lucasbmmn.overhearrserver.auth.util;

import fr.lucasbmmn.overhearrserver.auth.constant.AuthConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Utility component responsible for retrieving the JWT access token from an HTTP request.
 * <p>
 * It supports extracting the token from:
 * <ol>
 * <li>The {@code Authorization} header (Bearer token).</li>
 * <li>The {@code overhearr_access_token} cookie.</li>
 * </ol>
 * The Authorization header takes precedence over the cookie.
 * </p>
 */
@Component
public class TokenExtractor {
    /**
     * Extracts the JWT token string from the request.
     *
     * @param request The incoming {@link HttpServletRequest}.
     * @return The JWT string if found, or {@code null} if no token is present.
     */
    public String extract(HttpServletRequest request) {
        String token = null;

        String authHeader = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(AuthConstants.BEARER_PREFIX)) {
            token = authHeader.substring(AuthConstants.BEARER_PREFIX_LENGTH);
        }

        if (token == null && request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(cookie -> AuthConstants.ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        return token;
    }
}
