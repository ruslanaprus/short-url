package org.goit.urlshortener.url.service;

import lombok.extern.slf4j.Slf4j;
import org.goit.urlshortener.common.exception.ShortUrlException;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.function.Predicate;

import static org.goit.urlshortener.common.exception.ExceptionMessages.SHORT_CODE_ALREADY_EXISTS;

@Slf4j
@Component
public class ShortCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYabcdefghilkmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_SIZE = 6;
    private static final int MAX_ATTEMPTS = 100;
    private final SecureRandom random = new SecureRandom();

    public String generateUniqueShortCode(Predicate<String> existsChecker) {
        String shortCode;
        int attempts = 0;
        boolean exists;

        do {
            if (attempts >= MAX_ATTEMPTS) {
                log.error("Failed to generate a unique shortCode after {} attempts", MAX_ATTEMPTS);
                throw new ShortUrlException(SHORT_CODE_ALREADY_EXISTS.getMessage());
            }

            shortCode = generateRandomCode();
            attempts++;
            exists = existsChecker.test(shortCode);

            log.debug("Generated shortCode: {} (attempt {}), exists: {}", shortCode, attempts, exists);
        } while (exists);

        log.info("Unique shortCode generated after {} attempts: {}", attempts, shortCode);
        return shortCode;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_SIZE);
        for (int i = 0; i < SHORT_CODE_SIZE; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
