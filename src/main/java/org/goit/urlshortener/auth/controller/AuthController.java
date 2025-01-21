package org.goit.urlshortener.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.auth.model.mapper.LoginMapper;
import org.goit.urlshortener.auth.model.dto.LoginRequest;
import org.goit.urlshortener.auth.model.dto.SignupRequest;
import org.goit.urlshortener.auth.model.dto.LoginResponse;
import org.goit.urlshortener.auth.model.dto.SignupResponse;
import org.goit.urlshortener.auth.service.UserService;
import org.goit.urlshortener.auth.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Authentication controller", description = "Endpoints for user authentication and registration.")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginMapper loginMapper;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Sign up",
            description = "Register a new user in the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignupRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "email": "cat@gmail.com",
                                        "password": "VerySecretPassword1"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully registered.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SignupResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                 "email": "cat@gmail.com",
                                                 "message": "User created."
                                             }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "User already exists or invalid input", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Authenticate a user and generate a JWT token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "email": "cat@gmail.com",
                                        "password": "VerySecretPassword1"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully authenticated.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided", content = @Content),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                "error": "Invalid credentials"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return loginMapper.mapToResponse(jwtService.generateToken(userDetails));
    }
}
