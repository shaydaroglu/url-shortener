package com.sercan.url_shortener.domain;

import java.net.URL;
import java.time.OffsetDateTime;

public record ShortUrl(
        Long id,
        URL originalUrl,
        String shortCode,
        Boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt
) {

    public boolean isExpired() {
        return expiresAt.isBefore(OffsetDateTime.now());
    }
}
