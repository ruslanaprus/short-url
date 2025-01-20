package org.goit.urlshortener.exceptionHandler;

public class ShortUrlException extends RuntimeException {
    public ShortUrlException(String message) {
        super(message);
    }
}
