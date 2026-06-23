package com.sercan.url_shortener.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShortUrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "short_code", length = 4, unique = true)
    private String shortCode;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    public static ShortUrlEntity createWithoutShortCode(String originalUrl, OffsetDateTime expiresAt) {
        return new ShortUrlEntity(null, null, originalUrl, expiresAt);
    }

    public static ShortUrlEntity withShortCode(
            Long id,
            String shortCode,
            String originalUrl,
            OffsetDateTime expiresAt
    ) {
        return new ShortUrlEntity(id, shortCode, originalUrl, expiresAt);
    }
}
