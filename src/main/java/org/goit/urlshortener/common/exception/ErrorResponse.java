package org.goit.urlshortener.common.exception;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int httpStatus,
        String detail
) {
}