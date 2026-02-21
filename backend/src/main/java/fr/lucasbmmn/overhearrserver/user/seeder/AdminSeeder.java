package fr.lucasbmmn.overhearrserver.user.seeder;

import fr.lucasbmmn.overhearrserver.user.dto.UserCreationRequest;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import fr.lucasbmmn.overhearrserver.user.repository.UserRepository;
import fr.lucasbmmn.overhearrserver.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Component that runs on application startup to ensure a default administrator exists.
 * <p>
 * If the configured admin username is not found in the database, this seeder creates it.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserService userService;

    @Value("${overhearr.default-admin.username}")
    private String defaultAdminUsername;

    @Value("${overhearr.default-admin.password}")
    private String defaultAdminPassword;

    /**
     * Checks for the existence of the default admin user and creates it if missing.
     *
     * @param args command line arguments (unused).
     */
    @Override
    public void run(String... args) {
        if (userService.findByUsername(this.defaultAdminUsername).isEmpty()) {
            UserCreationRequest admin = new UserCreationRequest(
                    this.defaultAdminUsername,
                    this.defaultAdminPassword,
                    UserRole.ADMIN
            );

            this.userService.createUser(admin);
        }
    }
}