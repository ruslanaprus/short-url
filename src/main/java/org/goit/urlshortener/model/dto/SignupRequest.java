package org.goit.urlshortener.model.dto;

import lombok.Builder;

@Builder
public record SignupRequest(String email, String password) {
}
