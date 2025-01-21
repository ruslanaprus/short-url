package org.goit.urlshortener.auth.model.dto;

import org.goit.urlshortener.auth.model.mapper.LoginMapper;
import org.goit.urlshortener.auth.model.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginMapperTest {

    private final LoginMapper loginMapper = new LoginMapper();

    @Test
    @DisplayName("Should map token to LoginResponse")
    void testMapToResponse() {
        String token = "dummy_token";

        LoginResponse response = loginMapper.mapToResponse(token);

        assertEquals(token, response.token(), "The token in the response should match the input token");
    }
}