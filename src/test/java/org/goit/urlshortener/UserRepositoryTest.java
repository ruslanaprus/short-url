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

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Test findByEmail - when User exists")
    void testFindByEmail_userExists() {
        User user = new User("test@example.com", "Password1");
        userRepository.save(user);
        Optional<User> result = userRepository.findByEmail("test@example.com");
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Test findByEmail - when User does not exist")
    void testFindByEmail_userDoesNotExist() {

        Optional<User> result = userRepository.findByEmail("unknown@example.com");
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test existsByEmail - when Email exists")
    void testExistsByEmail_emailExists() {

        User user = new User("test@example.com", "Password1");
        userRepository.save(user);
        boolean exists = userRepository.existsByEmail("test@example.com");
        Assertions.assertTrue(exists);
    }

    @Test
    @DisplayName("Test existsByEmail - when Email does not exist")
    void testExistsByEmail_emailDoesNotExist() {

        boolean exists = userRepository.existsByEmail("unknown@example.com");
        Assertions.assertFalse(exists);
    }
}
