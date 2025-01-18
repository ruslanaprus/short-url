package org.goit.urlshortener.model.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(String token) {
}
