package org.goit.urlshortener.exceptionHandler;

import lombok.Getter;

@Getter
public enum ExceptionMessages {
    SHORT_CODE_ALREADY_EXISTS("Short code already exists"),
    INVALID_ORIGINAL_URL_DATA("Invalid original URL data"),
    INVALID_URL_ID_PROVIDED("Invalid URL ID provided"),
    URL_NOT_FOUND("URL not found"),
    USER_NOT_FOUND("User not found"),
    INVALID_USER_DATA("Invalid user data"),
    USER_ALREADY_EXISTS("User already exists");

    private final String message;

    ExceptionMessages(String message) {
        this.message = message;
    }

}
