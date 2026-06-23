package com.sercan.url_shortener.adapter.out.persistence;

import com.sercan.url_shortener.domain.ShortUrl;
import lombok.experimental.UtilityClass;

import java.net.MalformedURLException;
import java.net.URI;

@UtilityClass
public class ShortUrlMapper {
    public static ShortUrl toDomain(ShortUrlEntity entity) {
        return new ShortUrl(
                entity.getId(),
                URI.create(entity.getOriginalUrl()),
                entity.getShortCode(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
    }

    public static ShortUrlEntity toEntity(ShortUrl domain) {
        return new ShortUrlEntity(
                domain.id(),
                domain.originalUrl().toString(),
                domain.shortCode(),
                domain.isActive(),
                domain.createdAt(),
                domain.expiresAt()
        );
    }
}
