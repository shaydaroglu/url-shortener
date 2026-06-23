package com.sercan.url_shortener.application.service;

import com.sercan.url_shortener.application.port.in.ShortUrlUseCase;
import com.sercan.url_shortener.application.port.out.ShortUrlRepository;
import com.sercan.url_shortener.domain.ShortUrl;
import com.sercan.url_shortener.domain.exception.InvalidShortCodeGenerationException;
import com.sercan.url_shortener.domain.exception.NoShortCodesAvailableException;
import com.sercan.url_shortener.domain.exception.ShortCodeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlService implements ShortUrlUseCase {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int CODE_LENGTH = 4;
    private static final long BASE = ALPHABET.length();
    private static final long MAX_CODES = 14_776_336L; // 62^4

    private static final long MULTIPLIER = 1_000_003L;
    private static final long OFFSET = 2_754_231L;

    private final ShortUrlRepository shortUrlRepository;

    @Override
    @Transactional
    public ShortUrl createShortUrl(URI originalUri) {

        log.debug("Creating short url with original url {}", originalUri);
        ShortUrl savedWithoutCode = shortUrlRepository.save(
                new ShortUrl(
                        null,
                        originalUri,
                        null,
                        true,
                        null,
                        null
                )
        );

        String shortCode = generateShortCodeFromId(savedWithoutCode.id());

        ShortUrl savedWithCode = new ShortUrl(
                savedWithoutCode.id(),
                savedWithoutCode.originalUrl(),
                shortCode,
                savedWithoutCode.isActive(),
                savedWithoutCode.createdAt(),
                savedWithoutCode.expiresAt()
        );
        log.debug("Created short url with original url {}", originalUri);

        return shortUrlRepository.save(savedWithCode);
    }

    @Override
    public URI resolveShortCode(String shortCode) {
        Optional<ShortUrl> shortUrlOptional = shortUrlRepository.findByShortCode(shortCode);
        log.info("Getting original url for short code {}", shortCode);

        if (shortUrlOptional.isEmpty()) {
            log.warn("No short code found for short code {}", shortCode);
            throw new ShortCodeNotFoundException(shortCode);
        }

        return shortUrlOptional.get().originalUrl();
    }

    private static String generateShortCodeFromId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidShortCodeGenerationException(id);
        }

        long value = id - 1;

        if (value >= MAX_CODES) {
            throw new NoShortCodesAvailableException();
        }

        long scrambled = (value * MULTIPLIER + OFFSET) % MAX_CODES;

        return toFixedLengthBase62(scrambled);
    }

    private static String toFixedLengthBase62(long value) {
        StringBuilder result = new StringBuilder();

        do {
            int index = (int) (value % BASE);
            result.append(ALPHABET.charAt(index));
            value = value / BASE;
        } while (value > 0);

        while (result.length() < CODE_LENGTH) {
            result.append('0');
        }

        return result.reverse().toString();
    }


}
