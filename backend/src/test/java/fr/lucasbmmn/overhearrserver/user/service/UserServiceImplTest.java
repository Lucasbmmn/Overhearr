package fr.lucasbmmn.overhearrserver.user.service;

import fr.lucasbmmn.overhearrserver.user.dto.AdminUserUpdateRequest;
import fr.lucasbmmn.overhearrserver.user.dto.PasswordUpdateRequest;
import fr.lucasbmmn.overhearrserver.user.dto.UserCreationRequest;
import fr.lucasbmmn.overhearrserver.user.dto.UserUpdateRequest;
import fr.lucasbmmn.overhearrserver.user.exception.InvalidPasswordException;
import fr.lucasbmmn.overhearrserver.user.exception.SelfDeletionException;
import fr.lucasbmmn.overhearrserver.user.exception.UserAlreadyExistsException;
import fr.lucasbmmn.overhearrserver.user.exception.UserNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
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
        request = new UserCreationRequest(
                "newUser",
                "new@example.com",
                "StrongPass1!",
                UserRole.USER);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("newUser");
        user.setEmail("new@example.com");
        user.setPasswordHash("hashedOldPassword");
        user.setRole(UserRole.USER);
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");
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

        assertThrows(UserAlreadyExistsException.class, () ->
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

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.createUser(request)
        );
    }

    @Test
    void findAllUsers_ReturnsPage() {
        Page<User> expectedPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

        Page<User> result = userService.findAllUsers(0, 10, Sort.Direction.ASC);

        assertEquals(1, result.getTotalElements());
        assertEquals(user, result.getContent().getFirst());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void updateUser_AdminRequest_Success() {
        AdminUserUpdateRequest updateRequest = new AdminUserUpdateRequest(
                "updatedUser", "updated@example.com", UserRole.ADMIN);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("updatedUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(user.getId(), updateRequest);

        assertNotNull(updatedUser);
        assertEquals("updatedUser", updatedUser.getUsername());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals(UserRole.ADMIN, updatedUser.getRole());
    }

    @Test
    void updateUser_AdminRequest_UserNotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        AdminUserUpdateRequest updateRequest = new AdminUserUpdateRequest("updatedUser", null, UserRole.USER);
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(unknownId, updateRequest));
    }

    @Test
    void updateUser_UserRequest_Success() {
        UserUpdateRequest updateRequest = new UserUpdateRequest("updatedUser", "updated@example.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("updatedUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(user.getId(), updateRequest);

        assertNotNull(updatedUser);
        assertEquals("updatedUser", updatedUser.getUsername());
        assertEquals(UserRole.USER, updatedUser.getRole());
    }

    @Test
    void updateUser_DuplicateUsername_ThrowsException() {
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setUsername("takenUser");

        AdminUserUpdateRequest updateRequest = new AdminUserUpdateRequest("takenUser", null, UserRole.USER);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("takenUser")).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(user.getId(), updateRequest));
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsException() {
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail("taken@example.com");

        AdminUserUpdateRequest updateRequest = new AdminUserUpdateRequest("newUser", "taken@example.com",
                UserRole.USER);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(user.getId(), updateRequest));
    }

    @Test
    void updatePassword_Success() {
        PasswordUpdateRequest passwordRequest = new PasswordUpdateRequest("oldPassword", "NewPass1!");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "hashedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("NewPass1!")).thenReturn("hashedNewPassword");

        userService.updatePassword(user.getId(), passwordRequest);

        assertEquals("hashedNewPassword", user.getPasswordHash());
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_WrongCurrentPassword_ThrowsException() {
        PasswordUpdateRequest passwordRequest = new PasswordUpdateRequest("wrongPassword", "NewPass1!");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedOldPassword")).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userService.updatePassword(user.getId(), passwordRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePassword_UserNotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        PasswordUpdateRequest passwordRequest = new PasswordUpdateRequest("old", "NewPass1!");
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updatePassword(unknownId, passwordRequest));
    }

    @Test
    void deleteUser_Success() {
        UUID targetId = UUID.randomUUID();
        UUID requestingId = UUID.randomUUID();
        when(userRepository.existsById(targetId)).thenReturn(true);

        userService.deleteUser(targetId, requestingId);

        verify(userRepository).deleteById(targetId);
    }

    @Test
    void deleteUser_SelfDeletion_ThrowsException() {
        UUID sameId = UUID.randomUUID();

        assertThrows(SelfDeletionException.class, () -> userService.deleteUser(sameId, sameId));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        UUID targetId = UUID.randomUUID();
        UUID requestingId = UUID.randomUUID();
        when(userRepository.existsById(targetId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(targetId, requestingId));
        verify(userRepository, never()).deleteById(any());
    }
}