package org.goit.urlshortener.model.dto;

import lombok.Builder;

@Builder
public record JwtAuthenticationResponse(String token) {
}
