package org.goit.urlshortener.service.url;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        assertThrows(RuntimeException.class, () -> validator.validateUrl("invalid-url"));
    }

    @Test
    @DisplayName("Null URL should throw exception")
    void testNullUrl() {
        assertThrows(RuntimeException.class, () -> validator.validateUrl(null));
    }

    @Test
    @DisplayName("Empty URL should throw exception")
    void testEmptyUrl() {
        assertThrows(RuntimeException.class, () -> validator.validateUrl(""));
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
        assertThrows(RuntimeException.class, () -> validator.validateUrl("ftp://example.com"));
        assertThrows(RuntimeException.class, () -> validator.validateUrl("example.com"));
        assertThrows(RuntimeException.class, () -> validator.validateUrl("https://"));
    }
}
