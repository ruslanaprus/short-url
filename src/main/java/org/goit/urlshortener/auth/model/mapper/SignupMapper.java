package org.goit.urlshortener.auth.model.mapper;

import org.goit.urlshortener.auth.model.dto.SignupResponse;
import org.springframework.stereotype.Component;

@Component
public class SignupMapper {
    public SignupResponse mapToResponse(String email, String message) {
        return SignupResponse.builder()
                .email(email)
                .message(message)
                .build();
    }
}
