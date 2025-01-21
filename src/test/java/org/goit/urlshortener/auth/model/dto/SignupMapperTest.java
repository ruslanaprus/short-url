package org.goit.urlshortener.auth.model.dto;

import org.goit.urlshortener.auth.model.mapper.SignupMapper;
import org.goit.urlshortener.auth.model.dto.SignupResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignupMapperTest {

    private final SignupMapper signupMapper = new SignupMapper();

    @Test
    @DisplayName("Should map email and message to SignupResponse")
    void testMapToResponse() {
        String email = "test@example.com";
        String message = "User created";

        SignupResponse response = signupMapper.mapToResponse(email, message);

        assertEquals(email, response.email(), "The email in the response should match the input email");
        assertEquals(message, response.message(), "The message in the response should match the input message");
    }
}