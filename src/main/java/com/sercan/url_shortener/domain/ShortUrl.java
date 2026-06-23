package com.sercan.url_shortener.domain;

import java.net.URI;
import java.time.OffsetDateTime;

public record ShortUrl(
        Long id,
        URI originalUrl,
        String shortCode,
        OffsetDateTime createdAt
) {
}
