package org.goit.urlshortener.service;

import jakarta.persistence.EntityNotFoundException;
import org.goit.urlshortener.exceptionHandler.UserAlreadyExistsException;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.model.dto.SignupMapper;
import org.goit.urlshortener.model.dto.request.SignupRequest;
import org.goit.urlshortener.model.dto.response.SignupResponse;
import org.goit.urlshortener.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password1";
    private static final String UNKNOWN_EMAIL = "unknown@example.com";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SignupMapper signupMapper;

    @Test
    @DisplayName("Create User - when User already exists")
    void testCreateUser_alreadyExists() {

        SignupRequest request = new SignupRequest(TEST_EMAIL, TEST_PASSWORD);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(request)
        );

        assertEquals("User already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Create User - successful creation")
    void testCreateUser_successfulCreation() {

        SignupRequest request = new SignupRequest(TEST_EMAIL, TEST_PASSWORD);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(
                User.builder().email(TEST_EMAIL).password("encodedPassword").build()
        );
        when(signupMapper.mapToResponse(TEST_EMAIL, "User created")).thenReturn(
                new SignupResponse(TEST_EMAIL, "User created")
        );

        SignupResponse response = userService.createUser(request);
        assertNotNull(response);
        assertEquals(TEST_EMAIL, response.email());
        assertEquals("User created", response.message());
        verify(userRepository, times(1)).existsByEmail(TEST_EMAIL);
        verify(passwordEncoder, times(1)).encode(TEST_PASSWORD);
        verify(userRepository, times(1)).save(any(User.class));
        verify(signupMapper, times(1)).mapToResponse(request.email(), "User created");
    }

    @Test
    @DisplayName("Find User by Email - User found")
    void testFindUserByEmail() {
        User user = User.builder().email(TEST_EMAIL).password("encodedPassword").build();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserByEmail(TEST_EMAIL);

        assertTrue(result.isPresent());
        assertEquals(TEST_EMAIL, result.get().getEmail());
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("Find User by Email - User not found")
    void testFindUserByEmail_notFound() {

        when(userRepository.findByEmail(UNKNOWN_EMAIL)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.findUserByEmail(UNKNOWN_EMAIL)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(UNKNOWN_EMAIL);
    }
}
