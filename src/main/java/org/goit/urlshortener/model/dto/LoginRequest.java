package org.goit.urlshortener.model.dto;

import lombok.Builder;

@Builder
public record LoginRequest(String email, String password) {
}
