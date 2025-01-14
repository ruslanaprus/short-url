package org.goit.urlshortener.service;

import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.model.dto.SignupMapper;
import org.goit.urlshortener.model.dto.request.SignupRequest;
import org.goit.urlshortener.model.dto.response.SignupResponse;
import org.goit.urlshortener.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupMapper signupMapper;

    @Transactional
    public SignupResponse createUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("User already exists");
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        return signupMapper.mapToResponse(request.email(), "User created");
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User " + email + " not found"));
    }
}
