package com.sercan.url_shortener.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.net.URI;

public record CreateShortUrlRequest(
        @NotNull(message = "Original URL is required")
        @JsonProperty("original_url")
        URI originalUrl
) {
}
