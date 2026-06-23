package com.sercan.url_shortener.adapter.out.persistence;

import com.sercan.url_shortener.application.port.out.ShortUrlRepository;
import com.sercan.url_shortener.domain.ShortUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true"
})
@Import(ShortUrlPersistenceAdapter.class)
public class ShortUrlPersistenceAdapterTest {
    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Test
    void shouldSaveShortUrlWithoutShortCodeAndGenerateIdAndCreatedAt() {
        ShortUrl shortUrl = new ShortUrl(
                null,
                URI.create("https://google.com"),
                null,
                null
        );

        ShortUrl saved = shortUrlRepository.save(shortUrl);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.originalUrl()).isEqualTo(URI.create("https://google.com"));
        assertThat(saved.shortCode()).isNull();
    }

    @Test
    void shouldSaveAndFindByShortCode() {
        ShortUrl savedWithoutCode = shortUrlRepository.save(
                new ShortUrl(
                        null,
                        URI.create("https://google.com"),
                        null,
                        null
                )
        );

        ShortUrl savedWithCode = shortUrlRepository.save(
                new ShortUrl(
                        savedWithoutCode.id(),
                        savedWithoutCode.originalUrl(),
                        "byv5",
                        savedWithoutCode.createdAt()
                )
        );

        Optional<ShortUrl> found = shortUrlRepository.findByShortCode("byv5");

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(savedWithCode.id());
        assertThat(found.get().originalUrl()).isEqualTo(URI.create("https://google.com"));
        assertThat(found.get().shortCode()).isEqualTo("byv5");
    }
}
