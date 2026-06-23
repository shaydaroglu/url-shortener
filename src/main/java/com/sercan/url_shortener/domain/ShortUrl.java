package com.sercan.url_shortener.domain;

import java.net.URI;
import java.time.OffsetDateTime;

public record ShortUrl(
        Long id,
        URI originalUrl,
        String shortCode,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt
) {

    public boolean isExpired() {
        return expiresAt.isBefore(OffsetDateTime.now());
    }
}
