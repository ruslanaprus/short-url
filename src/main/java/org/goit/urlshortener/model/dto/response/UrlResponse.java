package org.goit.urlshortener.model.dto.response;

public record UrlResponse(String originalUrl,
                          String shortCode,
                          Long clickCount) {
}
