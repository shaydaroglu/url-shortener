package com.sercan.url_shortener.adapter.in.web;

import com.sercan.url_shortener.adapter.in.web.dto.CreateShortUrlRequest;
import com.sercan.url_shortener.adapter.in.web.dto.CreateShortUrlResponse;
import com.sercan.url_shortener.application.port.in.ShortUrlUseCase;
import com.sercan.url_shortener.domain.ShortUrl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerController {
    private final ShortUrlUseCase shortUrlUseCase;

    @PostMapping("/api/v1/urls")
    public ResponseEntity<CreateShortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request,
                                                                 HttpServletRequest servletRequest) {
        log.info("Creating short URL for originalUrl={}", request.originalUrl());

        URI original = URI.create(request.originalUrl());
        ShortUrl shortUrl = shortUrlUseCase.createShortUrl(original);
        URI shortenedUrl = ServletUriComponentsBuilder
                .fromRequestUri(servletRequest)
                .replacePath("/" + shortUrl.shortCode())
                .replaceQuery(null)
                .build()
                .toUri();

        CreateShortUrlResponse response = new CreateShortUrlResponse(
                shortUrl.originalUrl().toString(),
                shortUrl.shortCode(),
                shortenedUrl.toString());

        return ResponseEntity
                .created(shortenedUrl)
                .body(response);
    }

    @GetMapping("/{shortCode:[a-zA-Z0-9]{4}}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode
    ) {
        log.info("Redirect requested for shortCode={}", shortCode);
        URI target = shortUrlUseCase.resolveShortCode(shortCode);
        log.info("Redirecting shortcode: {} to: {}", shortCode, target);

        return ResponseEntity
                .status(HttpStatus.FOUND) // 302
                .location(target)
                .build();
    }
}
