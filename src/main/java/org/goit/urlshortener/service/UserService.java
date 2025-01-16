package org.goit.urlshortener.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.exceptionHandler.ExceptionMessages;
import org.goit.urlshortener.exceptionHandler.UserAlreadyExistsException;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.model.dto.SignupMapper;
import org.goit.urlshortener.model.dto.request.SignupRequest;
import org.goit.urlshortener.model.dto.response.SignupResponse;
import org.goit.urlshortener.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupMapper signupMapper;

    @Transactional
    public SignupResponse createUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(ExceptionMessages.USER_ALREADY_EXISTS.getMessage());
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        return signupMapper.mapToResponse(request.email(), "User created");
    }

    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email).
                orElseThrow(() -> new EntityNotFoundException
                        (String.valueOf(ExceptionMessages.USER_NOT_FOUND.getMessage()))));
    }
}
