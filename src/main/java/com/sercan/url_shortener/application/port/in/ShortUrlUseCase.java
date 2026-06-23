package com.sercan.url_shortener.application.port.in;

import com.sercan.url_shortener.domain.ShortUrl;

import java.net.URI;

public interface ShortUrlUseCase {
    ShortUrl createShortUrl(URI originalUri);
    URI resolveShortCode(String shortCode);
}
