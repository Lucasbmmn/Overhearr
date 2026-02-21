package fr.lucasbmmn.overhearrserver.config;

import fr.lucasbmmn.overhearrserver.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Main security configuration for the application.
 * <p>
 * This class configures the security filter chain, password encoding mechanisms,
 * and stateless session management. It integrates the {@link JwtAuthenticationFilter}
 * into the security pipeline.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${overhearr.password.encoder.argon2.memory}")
    private int memory;

    @Value("${overhearr.password.encoder.argon2.iterations}")
    private int iterations;

    @Value("${overhearr.password.encoder.argon2.parallelism}")
    private int parallelism;

    /**
     * Configures the password encoder using Argon2.
     *
     * @return An instance of {@link Argon2PasswordEncoder} configured with application properties.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, parallelism, memory, iterations);
    }

    /**
     * Defines the security filter chain.
     * <p>
     * Configuration includes:
     * <ul>
     * <li>Disabling CSRF and CORS.</li>
     * <li>Disabling default JSESSIONID session system.</li>
     * <li>Allowing public access to {@code /auth/login}.</li>
     * <li>Requiring authentication for all other requests.</li>
     * <li>Registering the custom JWT filter.</li>
     * </ul>
     *
     * @param http The {@link HttpSecurity} to configure.
     * @return The built {@link SecurityFilterChain}.
     * @throws Exception If an exception occurs.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/logout").permitAll()
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
