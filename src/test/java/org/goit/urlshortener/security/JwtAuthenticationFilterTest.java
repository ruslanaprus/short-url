package org.goit.urlshortener.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.goit.urlshortener.service.CustomUserDetailsService;
import org.goit.urlshortener.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should skip filter when authorization header is missing")
    void testAuthenticateWithMissingAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    @DisplayName("Should skip filter when authorization header is invalid")
    void testAuthenticateWithInvalidAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void shouldSetAuthenticationForValidToken() throws ServletException, IOException {
        // Mock request with valid Authorization header
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");

        // Mock JWT service behavior
        String userEmail = "test@example.com";
        when(jwtService.extractUserName("validToken")).thenReturn(userEmail);
        when(jwtService.isTokenValid(eq("validToken"), any(UserDetails.class))).thenReturn(true);

        // Mock UserDetailsService behavior
        UserDetails userDetails = new User(userEmail, "password", Collections.emptyList());
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() != null;
        assert SecurityContextHolder.getContext().getAuthentication().getName().equals(userEmail);
    }

    @Test
    void shouldNotSetAuthenticationForInvalidToken() throws ServletException, IOException {
        // Mock request with invalid Authorization header
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");

        // Mock JWT service behavior
        when(jwtService.extractUserName("invalidToken")).thenReturn("test@example.com");
        when(jwtService.isTokenValid(eq("invalidToken"), any(UserDetails.class))).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }
}