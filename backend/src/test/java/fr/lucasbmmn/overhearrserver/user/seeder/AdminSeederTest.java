package fr.lucasbmmn.overhearrserver.user.seeder;

import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import fr.lucasbmmn.overhearrserver.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminSeederTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminSeeder adminSeeder;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(adminSeeder, "defaultAdminUsername", "admin");
        ReflectionTestUtils.setField(adminSeeder, "defaultAdminPassword", "password");
    }

    @Test
    void run_AdminExist_ShouldCreateAdmin() {
        when(userService.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("hashPass");

        adminSeeder.run();

        verify(userService, times(1)).createUser(any());
    }

    @Test
    void run_AdminExist_ShouldDoNothing() {
        when(userService.findByUsername("admin"))
                .thenReturn(Optional.of(
                        new User(UUID.randomUUID(), "testuser", "email", "pass", UserRole.USER, Instant.now(), Instant.now())
                ));

        adminSeeder.run();

        verify(userService, never()).createUser(any());
    }
}