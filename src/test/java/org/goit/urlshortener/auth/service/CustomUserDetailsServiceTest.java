package org.goit.urlshortener.auth.service;

import org.goit.urlshortener.auth.repository.UserRepository;
import org.goit.urlshortener.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnUserDetailsWhenUserExists() {
        // Arrange
        String username = "test@example.com";
        String password = "password";

        // Mock the User entity to simulate the user found in the database
        User user = new User();
        user.setEmail(username);
        user.setPassword(password);

        // Mock the repository to return Optional of User
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails actualUser = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(actualUser, "UserDetails should not be null");
        assertEquals(username, actualUser.getUsername(), "Usernames should match");
        assertEquals(password, actualUser.getPassword(), "Passwords should match");
        verify(userRepository, times(1)).findByEmail(username);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistent@example.com";

        // Mock the repository to return Optional.empty for non-existent user
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username),
                "Expected UsernameNotFoundException for nonexistent user"
        );
        assertEquals("User not found", exception.getMessage(), "Exception message should match");
        verify(userRepository, times(1)).findByEmail(username);
    }
}