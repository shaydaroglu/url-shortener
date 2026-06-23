package com.sercan.url_shortener.domain.exception;

public class InvalidShortCodeGenerationException extends RuntimeException {
    public InvalidShortCodeGenerationException(Long id) {
        super("Cannot generate short code from invalid id: " + id);
    }
}
