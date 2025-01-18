package org.goit.urlshortener.service.url;

import org.goit.urlshortener.exceptionHandler.ShortUrlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.goit.urlshortener.exceptionHandler.ExceptionMessages.INVALID_ORIGINAL_URL_DATA;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlValidationServiceTest {

    private final UrlValidationService validator = new UrlValidationService();

    @Test
    @DisplayName("Valid URL should pass validation")
    void testValidUrl() {
        assertDoesNotThrow(() -> validator.validateUrl("https://example.com"));
    }

    @Test
    @DisplayName("Invalid URL should throw exception")
    void testInvalidUrl() {
        ShortUrlException exception = assertThrows(ShortUrlException.class,
                () -> validator.validateUrl("invalid-url"));
        assertEquals(INVALID_ORIGINAL_URL_DATA.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Null URL should throw exception")
    void testNullUrl() {
        ShortUrlException exception = assertThrows(ShortUrlException.class,
                () -> validator.validateUrl(null));
        assertEquals(INVALID_ORIGINAL_URL_DATA.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Empty URL should throw exception")
    void testEmptyUrl() {
        ShortUrlException exception = assertThrows(ShortUrlException.class,
                () -> validator.validateUrl(""));
        assertEquals(INVALID_ORIGINAL_URL_DATA.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Various valid URLs should pass validation")
    void testVariousValidUrls() {
        assertDoesNotThrow(() -> validator.validateUrl("http://example.com"));
        assertDoesNotThrow(() -> validator.validateUrl("https://example.com/path"));
        assertDoesNotThrow(() -> validator.validateUrl("https://example.com/path?query=1"));
    }

    @Test
    @DisplayName("Various invalid URLs should throw exception")
    void testVariousInvalidUrls() {
        assertThrows(ShortUrlException.class, () -> validator.validateUrl("ftp://example.com"));
        assertThrows(ShortUrlException.class, () -> validator.validateUrl("example.com"));
        assertThrows(ShortUrlException.class, () -> validator.validateUrl("https://"));
    }
}
