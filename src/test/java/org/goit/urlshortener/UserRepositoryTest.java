package org.goit.urlshortener;

import org.goit.urlshortener.model.User;
import org.goit.urlshortener.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.Assert.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail_userExists() {
        User user = new User("test@example.com", "Password1");
        userRepository.save(user);
        Optional<User> result = userRepository.findByEmail("test@example.com");
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testFindByEmail_userDoesNotExist() {

        Optional<User> result = userRepository.findByEmail("unknown@example.com");
        assertTrue(result.isEmpty());
    }

    @Test
    void testExistsByEmail_emailExists() {

        User user = new User("test@example.com", "Password1");
        userRepository.save(user);
        boolean exists = userRepository.existsByEmail("test@example.com");
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_emailDoesNotExist() {

        boolean exists = userRepository.existsByEmail("unknown@example.com");
        assertFalse(exists);
    }
}
