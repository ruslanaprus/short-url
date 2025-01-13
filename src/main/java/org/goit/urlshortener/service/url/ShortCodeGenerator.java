package org.goit.urlshortener.service.url;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.function.BooleanSupplier;

@Component
public class ShortCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYabcdefghilkmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public String generateUniqueShortCode(BooleanSupplier existsChecker) {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (existsChecker.getAsBoolean());
        return shortCode;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}

