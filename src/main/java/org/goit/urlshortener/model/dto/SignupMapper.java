package org.goit.urlshortener.model.dto;

import org.goit.urlshortener.model.dto.response.SignupResponse;
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
