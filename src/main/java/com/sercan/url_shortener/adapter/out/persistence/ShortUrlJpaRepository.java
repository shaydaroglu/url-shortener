package com.sercan.url_shortener.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlJpaRepository extends JpaRepository<ShortUrlEntity, Long> {
    Optional<ShortUrlEntity> findByShortCode(String shortCode);
}
