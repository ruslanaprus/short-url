package org.goit.urlshortener.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email
        @NotBlank(message = "Email must not be blank") String email,
        @NotBlank(message = "Password must not be blank") String password) {
}
