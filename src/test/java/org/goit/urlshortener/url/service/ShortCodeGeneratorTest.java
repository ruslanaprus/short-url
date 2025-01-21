package org.goit.urlshortener.url.service;

import org.goit.urlshortener.common.exception.ShortUrlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.goit.urlshortener.common.exception.ExceptionMessages.SHORT_CODE_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    private final ShortCodeGenerator generator = new ShortCodeGenerator();

    @Test
    @DisplayName("Generated short code should have a length of 6")
    void testGenerateShortCodeLength() {
        String shortCode = generator.generateUniqueShortCode(code -> false);
        assertEquals(6, shortCode.length());
    }

    @Test
    @DisplayName("Generated short codes should be unique")
    void testGenerateUniqueShortCodes() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String shortCode = generator.generateUniqueShortCode(codes::contains);
            assertFalse(codes.contains(shortCode));
            codes.add(shortCode);
        }
    }

    @Test
    @DisplayName("Generator should throw exception when max attempts exceeded")
    void testMaxAttemptsExceeded() {
        ShortUrlException ex = assertThrows(ShortUrlException.class,
                () -> generator.generateUniqueShortCode(code -> true));

        assertEquals(SHORT_CODE_ALREADY_EXISTS.getMessage(), ex.getMessage());
    }

    @Test
    @DisplayName("Generator should handle high load and produce 100,000 unique codes")
    void testGenerateShortCodeUnderHighLoad() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 100_000; i++) {
            String shortCode = generator.generateUniqueShortCode(codes::contains);
            assertFalse(codes.contains(shortCode));
            codes.add(shortCode);
        }
        assertEquals(100_000, codes.size());
    }
}
