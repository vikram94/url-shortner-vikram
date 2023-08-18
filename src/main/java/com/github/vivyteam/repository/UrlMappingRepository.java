package com.github.vivyteam.repository;


import com.github.vivyteam.dto.UrlMapping;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UrlMappingRepository extends ReactiveCrudRepository<UrlMapping, Long> {
    Mono<UrlMapping> findByLongUrl(String longUrl);
    Mono<UrlMapping> findByShortUrl(String shortUrl);
}