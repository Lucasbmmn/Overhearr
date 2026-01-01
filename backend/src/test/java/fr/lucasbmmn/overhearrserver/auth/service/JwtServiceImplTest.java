package fr.lucasbmmn.overhearrserver.auth.service;

import fr.lucasbmmn.overhearrserver.auth.model.CustomUserDetails;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    private static final String TEST_SECRET = Base64.getEncoder()
            .encodeToString(Jwts.SIG.HS256.key().build().getEncoded());

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "tokenSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "tokenExpiration", Duration.ofMinutes(10));
    }

    @Test
    void generateAccessToken_ShouldGenerateValidToken() {
        String token = jwtService.generateAccessToken("testuser");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateAccessToken("testuser");
        String username = jwtService.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void validateAccessToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtService.generateAccessToken("testuser");
        UserDetails userDetails = new CustomUserDetails(
                new User(UUID.randomUUID(), "testuser", "email", "pass", UserRole.USER, Instant.now(), Instant.now())
        );

        assertTrue(jwtService.validateAccessToken(token, userDetails));
    }

    @Test
    void validateAccessToken_ShouldReturnFalse_ForWrongUser() {
        String token = jwtService.generateAccessToken("testuser");
        UserDetails userDetails = new CustomUserDetails(
                new User(UUID.randomUUID(), "otheruser", "email", "pass", UserRole.USER, Instant.now(), Instant.now())
        );

        assertFalse(jwtService.validateAccessToken(token, userDetails));
    }

    @Test
    void validateAccessToken_ShouldReturnFalse_ForExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "tokenSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "tokenExpiration", Duration.ofSeconds(-1));

        String token = jwtService.generateAccessToken("testuser");

        UserDetails userDetails = new CustomUserDetails(
                new User(UUID.randomUUID(), "testuser", "email", "pass", UserRole.USER, Instant.now(), Instant.now())
        );

        assertFalse(jwtService.validateAccessToken(token, userDetails));
    }
}