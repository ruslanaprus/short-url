package org.goit.urlshortener.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.model.dto.LoginMapper;
import org.goit.urlshortener.model.dto.request.LoginRequest;
import org.goit.urlshortener.model.dto.request.SignupRequest;
import org.goit.urlshortener.model.dto.response.LoginResponse;
import org.goit.urlshortener.model.dto.response.SignupResponse;
import org.goit.urlshortener.service.UserService;
import org.goit.urlshortener.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginMapper loginMapper;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return loginMapper.mapToResponse(jwtService.generateToken(userDetails));
    }
}
