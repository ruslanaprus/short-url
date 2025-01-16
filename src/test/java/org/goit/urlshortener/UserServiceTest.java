package org.goit.urlshortener;

import jakarta.persistence.EntityNotFoundException;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.model.dto.request.SignupRequest;
import org.goit.urlshortener.repository.UserRepository;
import org.goit.urlshortener.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Create User - when User already exists")
    void testCreateUser_alreadyExists() {
        SignupRequest request = new SignupRequest("test@example.com", "Password1");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        String result = userService.createUser(request);

        assertEquals("User already exists", result);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Create User - successful creation")
    void testCreateUser_successfulCreation() {
        SignupRequest request = new SignupRequest("test@example.com", "Password1");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User("test@example.com", "Password1"));

        String result = userService.createUser(request);

        assertEquals("User created successfully", result);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Find User by Email - User found")
    void testFindUserByEmail() {
        String email = "test@example.com";
        User user = new User("test@example.com", "Password1");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserByEmail(email);

        assertEquals(Optional.of(user), result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Find User By Email - User not found")
    void testFindUserByEmail_notFound() {
        String email = "unknown@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.findUserByEmail(email)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
