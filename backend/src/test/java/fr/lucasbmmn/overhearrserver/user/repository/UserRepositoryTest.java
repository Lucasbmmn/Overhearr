package fr.lucasbmmn.overhearrserver.user.repository;

import fr.lucasbmmn.overhearrserver.AbstractIntegrationTest;
import fr.lucasbmmn.overhearrserver.user.model.User;
import fr.lucasbmmn.overhearrserver.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
        testUser.setPasswordHash("hashed_secret");
        testUser.setRole(UserRole.USER);

        entityManager.persistAndFlush(testUser);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUsernameExists() {
        Optional<User> found = userRepository.findByUsername("john_doe");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenUsernameDoesNotExist() {
        Optional<User> found = userRepository.findByUsername("unknown_user");

        assertThat(found).isEmpty();
    }

    @Test
    void findByUsernameOrEmail_ShouldReturnUser_WhenUsernameMatches() {
        Optional<User> found = userRepository.findByUsernameOrEmail("john_doe", "wrong@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john_doe");
    }

    @Test
    void findByUsernameOrEmail_ShouldReturnUser_WhenEmailMatches() {
        Optional<User> found = userRepository.findByUsernameOrEmail("wrong_name", "john@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findByUsernameOrEmail_ShouldReturnEmpty_WhenNeitherMatches() {
        Optional<User> found = userRepository.findByUsernameOrEmail("wrong_name", "wrong@example.com");

        assertThat(found).isEmpty();
    }
}