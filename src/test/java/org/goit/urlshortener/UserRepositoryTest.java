package org.goit.urlshortener;

import org.goit.urlshortener.model.User;
import org.goit.urlshortener.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class UserRepositoryTest {
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password1";
    private static final String UNKNOWN_EMAIL = "unknown@example.com";

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Test findByEmail - when User exists")
    void testFindByEmail_userExists() {
        User user = new User(TEST_EMAIL, TEST_PASSWORD);
        userRepository.save(user);
        Optional<User> result = userRepository.findByEmail(TEST_EMAIL);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(TEST_EMAIL, result.get().getEmail());
    }

    @Test
    @DisplayName("Test findByEmail - when User does not exist")
    void testFindByEmail_userDoesNotExist() {

        Optional<User> result = userRepository.findByEmail(UNKNOWN_EMAIL);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test existsByEmail - when Email exists")
    void testExistsByEmail_emailExists() {

        User user = new User(TEST_EMAIL, TEST_PASSWORD);
        userRepository.save(user);
        boolean exists = userRepository.existsByEmail(TEST_EMAIL);
        Assertions.assertTrue(exists);
    }

    @Test
    @DisplayName("Test existsByEmail - when Email does not exist")
    void testExistsByEmail_emailDoesNotExist() {

        boolean exists = userRepository.existsByEmail(UNKNOWN_EMAIL);
        Assertions.assertFalse(exists);
    }
}
