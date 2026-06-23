package com.sercan.url_shortener.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String title,
        int status,
        String detail,
        OffsetDateTime timestamp
) {
    public static ErrorResponse of(int status, String title, String detail) {
        return new ErrorResponse(
                title,
                status,
                detail,
                OffsetDateTime.now()
        );
    }
}
