package org.goit.urlshortener.service.url;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.util.InvalidUrlException;

@Slf4j
@Component
public class UrlValidationService {

    private static final String[] SCHEMES = {"http", "https"};
    private static final UrlValidator validator = new UrlValidator(SCHEMES);

    void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            log.warn("Validation failed: URL is null or blank");
            throw new InvalidUrlException("URL cannot be null or blank");
        }

        if (!validator.isValid(url)) {
            log.warn("Validation failed: Invalid URL format: {}", url);
            throw new InvalidUrlException("Invalid URL format: " + url);
        }

        log.debug("URL validation passed: {}", url);
    }
}

