package fr.lucasbmmn.overhearrserver.auth.filter;

import fr.lucasbmmn.overhearrserver.auth.model.CustomUserDetails;
import fr.lucasbmmn.overhearrserver.auth.service.JwtService;
import fr.lucasbmmn.overhearrserver.auth.util.TokenExtractor;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private TokenExtractor tokenExtractor;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ValidToken_AuthenticatesUser() throws ServletException, IOException {
        String token = "valid.token";
        UUID userId = UUID.randomUUID();
        String userIdString = userId.toString();
        UserDetails userDetails = new CustomUserDetails(
                new User(userId, "testuser", "email", "pass", UserRole.USER, Instant.now(), Instant.now())
        );

        when(tokenExtractor.extract(request)).thenReturn(token);
        when(jwtService.extractUserId(token)).thenReturn(userIdString);
        when(userDetailsService.loadUserByUsername(userIdString)).thenReturn(userDetails);
        when(jwtService.validateAccessToken(token, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userIdString, SecurityContextHolder.getContext().getAuthentication().getName());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoToken_ContinuesChain() throws ServletException, IOException {
        when(tokenExtractor.extract(request)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_DoesNotAuthenticate() throws ServletException, IOException {
        String token = "invalid.token";
        String username = "testuser";

        when(tokenExtractor.extract(request)).thenReturn(token);
        when(jwtService.extractUserId(token)).thenReturn(username);
        // Simulate validation failure
        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(new CustomUserDetails(
                        new User(UUID.randomUUID(), "testuser", "email", "pass", UserRole.USER, Instant.now(), Instant.now())
                ));
        when(jwtService.validateAccessToken(any(), any())).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}