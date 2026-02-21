package fr.lucasbmmn.overhearrserver.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.lucasbmmn.overhearrserver.auth.model.CustomUserDetails;
import fr.lucasbmmn.overhearrserver.auth.service.JwtService;
import fr.lucasbmmn.overhearrserver.auth.util.TokenExtractor;
import fr.lucasbmmn.overhearrserver.config.SecurityConfig;
import fr.lucasbmmn.overhearrserver.user.dto.*;
import fr.lucasbmmn.overhearrserver.user.mapper.UserMapper;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import fr.lucasbmmn.overhearrserver.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenExtractor tokenExtractor;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User testUser;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword");
        testUser.setRole(UserRole.USER);
        testUser.setCreatedAt(Instant.now());
        testUser.setUpdatedAt(Instant.now());

        testUserResponse = new UserResponse(
                testUser.getId(),
                testUser.getUsername(),
                testUser.getEmail(),
                testUser.getRole(),
                testUser.getCreatedAt(),
                testUser.getUpdatedAt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_AsAdmin_ReturnsPage() throws Exception {
        PageImpl<User> userPage = new PageImpl<>(List.of(testUser));

        when(userService.findAllUsers(anyInt(), anyInt(), any(Sort.Direction.class)))
                .thenReturn(userPage);
        when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].username").value("testuser"))
                .andExpect(jsonPath("$.meta.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_AsUser_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_AsAdmin_ReturnsCreated() throws Exception {
        UserCreationRequest createRequest = new UserCreationRequest(
                "newuser", "new@example.com", "StrongPass1!", UserRole.USER);

        when(userService.createUser(createRequest)).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUser_AsUser_ReturnsForbidden() throws Exception {
        UserCreationRequest createRequest = new UserCreationRequest(
                "newuser", "new@example.com", "StrongPass1!", UserRole.USER);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserById_AsAdmin_Successful() throws Exception {
        AdminUserUpdateRequest updateRequest = new AdminUserUpdateRequest(
                "updatedUser", "updated@example.com", UserRole.ADMIN);

        when(userService.updateUser(any(UUID.class), any(AdminUserUpdateRequest.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

        mockMvc.perform(put("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUserById_AsUser_ReturnsForbidden() throws Exception {
        AdminUserUpdateRequest updateRequest = new AdminUserUpdateRequest("updatedUser", null, UserRole.USER);

        mockMvc.perform(put("/users/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_AsAdmin_ReturnsNoContent() throws Exception {
        User adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setUsername("admin");
        adminUser.setPasswordHash("hash");
        adminUser.setRole(UserRole.ADMIN);
        CustomUserDetails adminPrincipal = new CustomUserDetails(adminUser);

        doNothing().when(userService).deleteUser(any(UUID.class), any(UUID.class));

        mockMvc.perform(delete("/users/{id}", UUID.randomUUID())
                .with(authentication(new UsernamePasswordAuthenticationToken(
                        adminPrincipal, null, adminPrincipal.getAuthorities()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_AsUser_ReturnsForbidden() throws Exception {
        User regularUser = new User();
        regularUser.setId(UUID.randomUUID());
        regularUser.setUsername("regular");
        regularUser.setPasswordHash("hash");
        regularUser.setRole(UserRole.USER);
        CustomUserDetails userPrincipal = new CustomUserDetails(regularUser);

        mockMvc.perform(delete("/users/{id}", UUID.randomUUID())
                .with(authentication(new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities()))))
                .andExpect(status().isForbidden());
    }
}
