package org.goit.urlshortener.exceptionHandler;

public class ShortUrlException extends RuntimeException {
    public ShortUrlException(ExceptionMessages message) {
        super(message.getMessage());
    }
}
