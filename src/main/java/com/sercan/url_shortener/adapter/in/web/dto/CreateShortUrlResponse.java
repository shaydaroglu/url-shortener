package com.sercan.url_shortener.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateShortUrlResponse(
        @JsonProperty("original_url")
        String originalUrl,
        @JsonProperty("short_code")
        String shortCode,
        @JsonProperty("shortened_url")
        String shortenedUrl
) {
}
