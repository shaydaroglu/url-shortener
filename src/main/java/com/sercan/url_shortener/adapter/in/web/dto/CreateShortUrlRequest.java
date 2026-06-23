package com.sercan.url_shortener.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateShortUrlRequest(
        @NotNull(message = "Original URL is required")
        @JsonProperty("original_url")
        @Pattern(
                regexp = "^https?://([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(/.*)?$",
                message = "URL must be a valid http or https URL"
        )
        String originalUrl
) {
}
