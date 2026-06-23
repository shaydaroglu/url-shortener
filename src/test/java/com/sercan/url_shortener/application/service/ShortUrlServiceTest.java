package com.sercan.url_shortener.application.service;

import com.sercan.url_shortener.application.port.out.ShortUrlRepository;
import com.sercan.url_shortener.domain.ShortUrl;
import com.sercan.url_shortener.domain.exception.InvalidShortCodeGenerationException;
import com.sercan.url_shortener.domain.exception.NoShortCodesAvailableException;
import com.sercan.url_shortener.domain.exception.ShortCodeNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ShortUrlServiceTest {
    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Test
    void shouldCreateShortUrlWithGeneratedShortCode() {
        ShortUrlService service = new ShortUrlService(shortUrlRepository);

        URI originalUrl = URI.create("https://google.com");
        OffsetDateTime createdAt = OffsetDateTime.now();

        when(shortUrlRepository.save(any(ShortUrl.class)))
                .thenAnswer(invocation -> {
                    ShortUrl shortUrl = invocation.getArgument(0);

                    if (shortUrl.id() == null) {
                        return new ShortUrl(
                                1L,
                                shortUrl.originalUrl(),
                                null,
                                shortUrl.isActive(),
                                createdAt,
                                shortUrl.expiresAt()
                        );
                    }

                    return shortUrl;
                });

        ShortUrl result = service.createShortUrl(originalUrl);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.originalUrl()).isEqualTo(originalUrl);
        assertThat(result.shortCode()).isEqualTo("byv5");
        assertThat(result.isActive()).isTrue();

        ArgumentCaptor<ShortUrl> captor = ArgumentCaptor.forClass(ShortUrl.class);
        verify(shortUrlRepository, times(2)).save(captor.capture());

        ShortUrl firstSave = captor.getAllValues().get(0);
        ShortUrl secondSave = captor.getAllValues().get(1);

        assertThat(firstSave.id()).isNull();
        assertThat(firstSave.shortCode()).isNull();

        assertThat(secondSave.id()).isEqualTo(1L);
        assertThat(secondSave.shortCode()).isEqualTo("byv5");
    }

    @Test
    void shouldGenerateDifferentShortCodesForDifferentIds() {
        ShortUrlService service = new ShortUrlService(shortUrlRepository);

        URI originalUrl = URI.create("https://youtube.com");
        OffsetDateTime createdAt = OffsetDateTime.now();

        when(shortUrlRepository.save(any(ShortUrl.class)))
                .thenAnswer(invocation -> {
                    ShortUrl shortUrl = invocation.getArgument(0);

                    if (shortUrl.id() == null) {
                        return new ShortUrl(
                                2L,
                                shortUrl.originalUrl(),
                                null,
                                shortUrl.isActive(),
                                createdAt,
                                shortUrl.expiresAt()
                        );
                    }

                    return shortUrl;
                });

        ShortUrl result = service.createShortUrl(originalUrl);

        assertThat(result.shortCode()).isEqualTo("fKEa");
    }

    @Test
    void shouldThrowWhenNoShortCodesAreAvailable() {
        ShortUrlService service = new ShortUrlService(shortUrlRepository);

        URI originalUrl = URI.create("https://google.com");

        when(shortUrlRepository.save(any(ShortUrl.class)))
                .thenReturn(new ShortUrl(
                        14_776_337L,
                        originalUrl,
                        null,
                        true,
                        OffsetDateTime.now(),
                        null
                ));

        assertThatThrownBy(() -> service.createShortUrl(originalUrl))
                .isInstanceOf(NoShortCodesAvailableException.class);

        verify(shortUrlRepository, times(1)).save(any(ShortUrl.class));
    }

    @Test
    void shouldResolveShortCodeToOriginalUrl() {
        ShortUrlService service = new ShortUrlService(shortUrlRepository);

        URI originalUrl = URI.create("https://youtube.com");

        when(shortUrlRepository.findByShortCode("byv5"))
                .thenReturn(Optional.of(new ShortUrl(
                        1L,
                        originalUrl,
                        "byv5",
                        true,
                        OffsetDateTime.now(),
                        null
                )));

        URI result = service.resolveShortCode("byv5");

        assertThat(result).isEqualTo(originalUrl);
    }

    @Test
    void shouldThrowWhenShortCodeDoesNotExist() {
        ShortUrlService service = new ShortUrlService(shortUrlRepository);

        when(shortUrlRepository.findByShortCode("xxxx"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resolveShortCode("xxxx"))
                .isInstanceOf(ShortCodeNotFoundException.class);
    }

    @Test
    void shouldThrowWhenGeneratedIdIsInvalid() {
        ShortUrlService service = new ShortUrlService(shortUrlRepository);

        URI originalUrl = URI.create("https://youtube.com");

        when(shortUrlRepository.save(any(ShortUrl.class)))
                .thenReturn(new ShortUrl(
                        0L,
                        originalUrl,
                        null,
                        true,
                        OffsetDateTime.now(),
                        null
                ));

        assertThatThrownBy(() -> service.createShortUrl(originalUrl))
                .isInstanceOf(InvalidShortCodeGenerationException.class)
                .hasMessage("Cannot generate short code from invalid id: 0");
    }
}
