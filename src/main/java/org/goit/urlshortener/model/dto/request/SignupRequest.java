package org.goit.urlshortener.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SignupRequest(
        @Email
        @NotBlank(message = "Email must not be blank") String email,
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = """
                    Password must meet the following requirements:
                    - At least 8 characters in length
                    - At least one lowercase letter
                    - At least one uppercase letter
                    - At least one number
                    """
        )
        @NotBlank(message = "Password must not be blank") String password) {}

