package fr.lucasbmmn.overhearrserver.user.service;

import fr.lucasbmmn.overhearrserver.user.dto.UserCreationRequest;
import fr.lucasbmmn.overhearrserver.user.mapper.UserMapper;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(@NonNull UserCreationRequest request) {
        if (userRepository.findByUsernameOrEmail(request.getUsername(),
                request.getEmail() != null ? request.getEmail() : request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with this username or email already exists");
        }

        User user = this.userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating user", e);
            throw new IllegalArgumentException("User with this username or email already exists");
        }
    }

    @Override
    public Optional<User> findByUsernameOrEmail(@NonNull String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier);
    }

    @Override
    public Optional<User> findByUsername(@NonNull String username) {
        return userRepository.findByUsername(username);
    }
}