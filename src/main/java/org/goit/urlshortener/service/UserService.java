package org.goit.urlshortener.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.model.dto.SignupRequest;
import org.goit.urlshortener.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public String createUser(SignupRequest request) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.email()))) {
            return ("User with email " + request.email() + " already exists");
        }
        String encodedPassword = passwordEncoder.encode(request.password());
        var user = new User(request.email(), encodedPassword);
        userRepository.save(user);
        return "User registered successfully";
    }

    public User findByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User " + userEmail + " not found"));
    }
}
