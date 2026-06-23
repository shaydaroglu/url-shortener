package com.sercan.url_shortener.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateShortUrlRequest(
        @NotNull(message = "Field 'original_url' is required")
        @JsonProperty("original_url")
        @Size(
                max = 2048,
                message = "Original URL must not exceed 2048 characters"
        )
        @Pattern(
                regexp = "^https?://([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(/.*)?$",
                message = "URL must be a valid http or https URL"
        )
        String originalUrl
) {
}
