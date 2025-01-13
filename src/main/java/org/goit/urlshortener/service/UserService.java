package org.goit.urlshortener.service;

import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.model.request.UserCreateRequest;
import org.goit.urlshortener.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String createUser(UserCreateRequest request) {
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
        return userRepository.findByEmail(email);
    }
}
