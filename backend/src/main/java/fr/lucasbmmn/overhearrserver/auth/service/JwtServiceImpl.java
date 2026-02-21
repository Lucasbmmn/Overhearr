package fr.lucasbmmn.overhearrserver.auth.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.function.Function;

/**
 * Implementation of the {@link JwtService} for handling JSON Web Tokens.
 * <p>
 * Uses the JJWT library to generate, sign, and validate tokens using an HMAC-SHA algorithm.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${overhearr.token.jwt.expiration:730d}")
    private Duration tokenExpiration;

    @Value("${overhearr.token.jwt.secret}")
    private String tokenSecret;

    @Override
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateAccessToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration.toMillis()))
                .signWith(this.getSigningKey())
                .compact();
    }

    @Override
    public boolean validateAccessToken(String token, UserDetails userDetails) {
        try {
            final String userId = this.extractUserId(token);
            return userId.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Helper method to extract the expiration date from the token.
     *
     * @param token The JWT string.
     * @return The expiration {@link Date} of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Helper method to extracts a specific claim from a JWT using a claims resolver function.
     *
     * @param token          The JWT string.
     * @param claimsResolver A function to apply to the claims.
     * @param <T>            The type of the claim to be returned.
     * @return The resolved claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT to extract all claims (payload).
     * <p>
     * This method verifies the token's signature using the signing key.
     * </p>
     *
     * @param token The JWT string.
     * @return The {@link Claims} object containing the payload.
     * @throws JwtException if the token is invalid or the signature verification fails.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Generates the cryptographic key used for signing the JWT.
     * <p>
     * Decodes the Base64-encoded secret from the application properties and creates
     * an HMAC-SHA key.
     * </p>
     *
     * @return The {@link SecretKey} for signing/verifying tokens.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Checks if the token has passed its expiration time.
     *
     * @param token The JWT string.
     * @return {@code true} if the current time is after the token's expiration, {@code false} otherwise.
     */
    private boolean isTokenExpired(String token) {
        return this.extractExpiration(token).before(new Date());
    }
}
