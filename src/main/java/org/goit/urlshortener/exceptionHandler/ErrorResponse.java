package org.goit.urlshortener.exceptionHandler;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int httpStatus,
        String detail
) {
}