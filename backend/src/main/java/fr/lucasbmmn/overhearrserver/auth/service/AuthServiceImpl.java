package fr.lucasbmmn.overhearrserver.auth.service;

import fr.lucasbmmn.overhearrserver.auth.dto.AuthResponse;
import fr.lucasbmmn.overhearrserver.user.mapper.UserMapper;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link AuthService}.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(@NonNull String identifier, @NonNull String password) {
        User user = userService.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateAccessToken(user.getId().toString());

        return new AuthResponse(token, userMapper.toResponse(user));
    }
}