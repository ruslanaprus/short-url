package org.goit.urlshortener.url.model.dto;

public record UrlResponse(String originalUrl,
                          String shortCode,
                          Long clickCount) {
}
