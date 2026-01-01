package fr.lucasbmmn.overhearrserver.user.service;

import fr.lucasbmmn.overhearrserver.user.dto.UserCreationRequest;
import fr.lucasbmmn.overhearrserver.user.mapper.UserMapper;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import fr.lucasbmmn.overhearrserver.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreationRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        request = new UserCreationRequest();
        request.setUsername("newUser");
        request.setEmail("new@example.com");
        request.setPassword("StrongPass1!");
        request.setRole(UserRole.USER);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("newUser");
        user.setEmail("new@example.com");
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(request);

        assertNotNull(createdUser);
        assertEquals("newUser", createdUser.getUsername());
        verify(passwordEncoder).encode("StrongPass1!");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_DuplicateUser_ThrowsException() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(request)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_DataIntegrityViolation_ThrowsException() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(request)
        );
    }
}