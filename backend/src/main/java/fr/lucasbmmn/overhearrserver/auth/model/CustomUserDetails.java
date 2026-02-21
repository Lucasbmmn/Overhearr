package fr.lucasbmmn.overhearrserver.auth.model;

import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Adapter that wraps a {@link User} entity to conform to Spring Security's {@link UserDetails} contract.
 * <p>
 * The username is mapped to the user's UUID, and the password to the stored hash.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * Returns the authorities granted to the user based on their {@link UserRole}.
     *
     * @return A singleton list containing the user's role prefixed with {@code ROLE_}.
     */
    @NonNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    /**
     * Returns the hashed password used to authenticate the user.
     *
     * @return The user's password hash.
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Returns the user's unique identifier as a string, used as the principal name.
     *
     * @return The string representation of the user's UUID.
     */
    @NonNull
    @Override
    public String getUsername() {
        return user.getId().toString();
    }
}