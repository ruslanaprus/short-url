package org.goit.urlshortener.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record UrlUpdateRequest(
        @URL(message = "Invalid URL format")
        @NotNull(message = "Title must not be null")
        @NotEmpty(message = "Title must not be empty") String originalUrl,

        @NotNull(message = "Title must not be null")
        @NotEmpty(message = "Title must not be empty") String shortCode) {}