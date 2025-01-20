package org.goit.urlshortener.service.url;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.goit.urlshortener.exceptionHandler.ShortUrlException;
import org.springframework.stereotype.Component;

import static org.goit.urlshortener.exceptionHandler.ExceptionMessages.INVALID_ORIGINAL_URL_DATA;

@Slf4j
@Component
public class UrlValidationService {

    private static final String[] SCHEMES = {"http", "https"};
    private static final UrlValidator validator = new UrlValidator(SCHEMES);

    void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            log.warn("Validation failed: URL is null or blank");
            throw new ShortUrlException(INVALID_ORIGINAL_URL_DATA.getMessage());
        }

        if (!validator.isValid(url)) {
            log.warn("Validation failed: Invalid URL format: {}", url);
            throw new ShortUrlException(INVALID_ORIGINAL_URL_DATA.getMessage());
        }

        log.debug("URL validation passed: {}", url);
    }
}


