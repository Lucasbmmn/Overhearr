package fr.lucasbmmn.overhearrserver.auth.service;

import fr.lucasbmmn.overhearrserver.auth.dto.AuthResponse;
import fr.lucasbmmn.overhearrserver.user.dto.UserResponse;
import fr.lucasbmmn.overhearrserver.user.mapper.UserMapper;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import fr.lucasbmmn.overhearrserver.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPasswordHash("hashedPassword");
        user.setRole(UserRole.USER);

        userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void login_Successful() {
        when(userService.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken("testuser")).thenReturn("token");
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        AuthResponse response = authService.login("testuser", "password");

        assertNotNull(response);
        assertEquals("token", response.getAccessToken());
        assertEquals("testuser", response.getUser().getUsername());
        verify(jwtService).generateAccessToken("testuser");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userService.findByUsernameOrEmail("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                authService.login("unknown", "password")
        );
    }

    @Test
    void login_BadCredentials_ThrowsException() {
        when(userService.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () ->
                authService.login("testuser", "wrongpassword")
        );
    }
}