package com.sercan.url_shortener.domain.exception;

public class ShortCodeNotFoundException extends RuntimeException {
    public ShortCodeNotFoundException(String shortCode) {
        super("Could not find short code [" + shortCode + "]");
    }
}
