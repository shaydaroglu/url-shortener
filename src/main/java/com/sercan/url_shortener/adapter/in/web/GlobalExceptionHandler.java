package com.sercan.url_shortener.adapter.in.web;

import com.sercan.url_shortener.adapter.in.web.dto.ErrorResponse;
import com.sercan.url_shortener.domain.exception.InvalidShortCodeGenerationException;
import com.sercan.url_shortener.domain.exception.NoShortCodesAvailableException;
import com.sercan.url_shortener.domain.exception.ShortCodeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleShortCodeNotFoundException(ShortCodeNotFoundException ex) {
        return build(
                HttpStatus.NOT_FOUND,
                "Short code not found",
                ex.getMessage());
    }

    @ExceptionHandler(NoShortCodesAvailableException.class)
    public ResponseEntity<ErrorResponse> handleNoShortCodesAvailable(
            NoShortCodesAvailableException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(
                        HttpStatus.CONFLICT.value(),
                        "No short codes available",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(InvalidShortCodeGenerationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidShortCodeGeneration(
            InvalidShortCodeGenerationException exception
    ) {
        log.error("Short code generation failed", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Short code generation failed",
                        "Could not generate short URL"
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.of(HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        detail)
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid request body",
                        "Request body is missing or malformed"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        log.error("Unexpected error occurred", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Unexpected server error",
                        "Something went wrong"
                ));
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String title,
            String detail
    ) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(ErrorResponse.of(status.value(), title, detail));
    }
}
