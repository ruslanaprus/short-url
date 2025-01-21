package org.goit.urlshortener.common.exception;

public class ShortUrlException extends RuntimeException {
    public ShortUrlException(String message) {
        super(message);
    }
}
