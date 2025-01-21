package org.goit.urlshortener.auth.model.dto;

import lombok.Builder;

@Builder
public record LoginResponse(String token) {
}
