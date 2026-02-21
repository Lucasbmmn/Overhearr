package fr.lucasbmmn.overhearrserver.auth.service;

import fr.lucasbmmn.overhearrserver.user.repository.UserRepository;
import fr.lucasbmmn.overhearrserver.auth.model.CustomUserDetails;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of Spring Security's {@link UserDetailsService}.
 * <p>
 * Loads user details by their UUID (used as the username in the security context)
 * from the {@link UserRepository}.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user details by the user's UUID string.
     *
     * @param username The string representation of the user's UUID.
     * @return The {@link UserDetails} wrapping the found user.
     * @throws UsernameNotFoundException if no user exists with the given UUID.
     */
    @NonNull
    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return this.userRepository.findById(UUID.fromString(username))
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with id: " + username));
    }
}
