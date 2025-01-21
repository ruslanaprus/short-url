package org.goit.urlshortener.auth.controller;

import org.goit.urlshortener.TestcontainersConfiguration;
import org.goit.urlshortener.common.exception.UserAlreadyExistsException;
import org.goit.urlshortener.auth.model.mapper.LoginMapper;
import org.goit.urlshortener.auth.model.dto.LoginRequest;
import org.goit.urlshortener.auth.model.dto.SignupRequest;
import org.goit.urlshortener.auth.model.dto.LoginResponse;
import org.goit.urlshortener.auth.model.dto.SignupResponse;
import org.goit.urlshortener.auth.service.JwtService;
import org.goit.urlshortener.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private LoginMapper loginMapper;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("POST /api/v1/signup - Should create a new user")
    void testSignupSuccess() throws Exception {
        SignupRequest request = new SignupRequest("cat@gmail.com", "Qwerty1234");
        SignupResponse response = new SignupResponse("cat@gmail.com", "User created");
        when(userService.createUser(request)).thenReturn(response);
        mockMvc.perform(post("/api/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\": \"cat@gmail.com\"," +
                                "\"password\": \"Qwerty1234\"}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("cat@gmail.com"))
                .andExpect(jsonPath("$.message").value("User created"));
    }

    @Test
    @DisplayName("Sign up for already existing user")
    void testSignupUserAlreadyExists() {
        SignupRequest request = new SignupRequest("existing@example.com", "Qwerty1234");
        when(userService.createUser(request)).thenThrow(new UserAlreadyExistsException("User already exists"));
        try {
            authController.signup(request);
        } catch (UserAlreadyExistsException ex) {
            assertEquals("User already exists", ex.getMessage());
        }
        verify(userService, times(1)).createUser(request);
    }
    @Test
    @DisplayName("Successful login")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        String token = "dummy_token";
        LoginResponse response = new LoginResponse(token);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(token);
        when(loginMapper.mapToResponse(token)).thenReturn(response);
        LoginResponse result = authController.login(request);
        assertEquals(response.token(), result.token());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(userDetails);
        verify(loginMapper, times(1)).mapToResponse(token);
    }
    @Test
    @DisplayName("Failed login")
    void testLoginBadCredentials() {
        LoginRequest request = new LoginRequest("wrong@mail.com", "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        try {
            authController.login(request);
        } catch (BadCredentialsException ex) {
            assertEquals("Bad credentials", ex.getMessage());
        }
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}