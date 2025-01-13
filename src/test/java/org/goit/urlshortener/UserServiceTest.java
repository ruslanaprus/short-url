package org.goit.urlshortener;

import org.goit.urlshortener.model.User;
import org.goit.urlshortener.model.request.UserCreateRequest;
import org.goit.urlshortener.repository.UserRepository;
import org.goit.urlshortener.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void testCreateUser_alreadyExists() {
        UserCreateRequest request = new UserCreateRequest("test@example.com", "Password1");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        String result = userService.createUser(request);
        assertEquals("User already exists", result);
    }

    @Test
    void testCreateUser_successfulCreation() {
        UserCreateRequest request = new UserCreateRequest("test@example.com", "Password1");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User("test@example.com", "Password1"));
        String result = userService.createUser(request);
        assertEquals("User created successfully", result);
    }

    @Test
    void testFindUserByEmail() {
        String email = "test@example.com";
        User user = new User("test@example.com", "Password1");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findUserByEmail(email);
        assertEquals(Optional.of(user), result);
    }

    @Test
    void testFindUserByEmail_notFound() {
        String email = "unknown@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        Optional<User> result = userService.findUserByEmail(email);
        assertEquals(Optional.empty(), result);
    }
}


