package com.sercan.url_shortener.domain.exception;

public class NoShortCodesAvailableException extends IllegalStateException {
    public NoShortCodesAvailableException() {
        super("No more 4-character short codes available");
    }
}
