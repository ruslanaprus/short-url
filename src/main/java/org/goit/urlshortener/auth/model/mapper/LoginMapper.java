package org.goit.urlshortener.auth.model.mapper;

import org.goit.urlshortener.auth.model.dto.LoginResponse;
import org.springframework.stereotype.Component;

@Component
public class LoginMapper {
    public LoginResponse mapToResponse(String token) {
        return LoginResponse.builder()
                .token(token)
                .build();
    }
}
