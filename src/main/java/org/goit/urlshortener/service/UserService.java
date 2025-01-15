package org.goit.urlshortener.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.ExceptionMessages;
import org.goit.urlshortener.SignupRequest;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public String createUser(SignupRequest request ) {
        if (userRepository.existsByEmail(request.email())) {
            return "User already exists";
        }
        var user = User.builder()
                .email(request.email())
                .password(request.password())
                .build();
        userRepository.save(user);

        return "User created successfully";
    }

    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email).
                orElseThrow(() -> new EntityNotFoundException
                        (String.valueOf(ExceptionMessages.USER_NOT_FOUND.getMessage()))));
    }
}

