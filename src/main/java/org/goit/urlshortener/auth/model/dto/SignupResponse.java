package org.goit.urlshortener.auth.model.dto;

import lombok.Builder;

@Builder
public record SignupResponse(String email,
                             String message) {}
