package fr.lucasbmmn.overhearrserver.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.lucasbmmn.overhearrserver.auth.dto.AuthResponse;
import fr.lucasbmmn.overhearrserver.auth.dto.LoginRequest;
import fr.lucasbmmn.overhearrserver.auth.service.AuthService;
import fr.lucasbmmn.overhearrserver.auth.service.JwtService;
import fr.lucasbmmn.overhearrserver.auth.util.TokenExtractor;
import fr.lucasbmmn.overhearrserver.config.SecurityConfig;
import fr.lucasbmmn.overhearrserver.user.dto.UserResponse;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenExtractor tokenExtractor;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void loginUser_Successful() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "password");

        UserResponse userResponse = new UserResponse(
                UUID.randomUUID(), "testuser", UserRole.USER, Instant.now(), Instant.now()
        );

        AuthResponse authResponse = new AuthResponse("token", userResponse);

        when(authService.login(anyString(), anyString())).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", containsString("overhearr_access_token=token")));
    }

    @Test
    @WithMockUser
    void logout_Successful() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));
    }
}