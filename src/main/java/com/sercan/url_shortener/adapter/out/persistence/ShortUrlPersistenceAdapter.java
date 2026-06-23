package com.sercan.url_shortener.adapter.out.persistence;

import com.sercan.url_shortener.application.port.out.ShortUrlRepository;
import com.sercan.url_shortener.domain.ShortUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ShortUrlPersistenceAdapter implements ShortUrlRepository {
    private final ShortUrlJpaRepository repository;

    @Override
    public ShortUrl save(ShortUrl shortUrl) {
        ShortUrlEntity entity = ShortUrlMapper.toEntity(shortUrl);

        ShortUrlEntity saved = repository.saveAndFlush(entity);

        return repository.findById(saved.getId())
                .map(ShortUrlMapper::toDomain)
                .orElseThrow();
    }

    @Override
    public Optional<ShortUrl> findByShortCode(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(ShortUrlMapper::toDomain);
    }
}
