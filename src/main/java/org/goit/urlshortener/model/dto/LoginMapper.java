package org.goit.urlshortener.model.dto;

import org.goit.urlshortener.model.dto.response.LoginResponse;
import org.springframework.stereotype.Component;

@Component
public class LoginMapper {
    public LoginResponse mapToResponse(String token) {
        return LoginResponse.builder()
                .token(token)
                .build();
    }
}
