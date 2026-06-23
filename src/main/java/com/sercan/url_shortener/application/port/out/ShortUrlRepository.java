package com.sercan.url_shortener.application.port.out;

import com.sercan.url_shortener.domain.ShortUrl;

import java.util.Optional;

public interface ShortUrlRepository {
    ShortUrl save(ShortUrl shortUrl);
    Optional<ShortUrl> findByShortCode(String shortCode);
}
