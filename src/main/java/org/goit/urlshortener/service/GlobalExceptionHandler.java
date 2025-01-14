package org.goit.urlshortener.service;

import org.goit.urlshortener.ExceptionMessages;

public class GlobalExceptionHandler extends RuntimeException {
    public GlobalExceptionHandler(ExceptionMessages exceptionMessages) {
        super(exceptionMessages.getMessage());
    }
}
