package fr.lucasbmmn.overhearrserver.auth.service;

import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import fr.lucasbmmn.overhearrserver.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setPasswordHash("hashedPass");
        user.setRole(UserRole.USER);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId.toString());

        assertNotNull(userDetails);
        assertEquals(userId.toString(), userDetails.getUsername());
        assertEquals("hashedPass", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_NotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();

        when(userRepository.findById(unknownId))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(unknownId.toString()));
    }
}