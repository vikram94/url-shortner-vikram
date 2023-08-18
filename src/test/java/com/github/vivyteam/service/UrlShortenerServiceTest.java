package com.github.vivyteam.service;

import com.github.vivyteam.dto.UrlMapping;
import com.github.vivyteam.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UrlShortenerServiceTest {

    @InjectMocks
    @Spy
    private UrlShortenerService urlShortenerService;

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(urlShortenerService, "baseUrl", "http://localhost:9000/");
    }

    @Test
    void testShortenUrl() {
        String longUrl = "https://github.com/VivyTeam/url-shortener-be-test";
        String shortUrl = "http://localhost:9000/b";

        UrlMapping savedMapping = new UrlMapping(1L, longUrl, null);

        Mockito.when(urlMappingRepository.findByLongUrl(Mockito.anyString()))
                .thenReturn(Mono.empty());
        Mockito.when(urlMappingRepository.save(Mockito.any(UrlMapping.class)))
                .thenReturn(Mono.just(savedMapping));

        StepVerifier.create(urlShortenerService.shortenUrl(savedMapping))
                .expectNext(shortUrl)
                .verifyComplete();
    }

    @Test
    void testGetLongUrl() {
        String shortUrl = "abc123";
        String longUrl = "https://github.com/VivyTeam/url-shortener-be-test";

        UrlMapping savedMapping = new UrlMapping(1L, longUrl, shortUrl);

        Mockito.when(urlMappingRepository.findByShortUrl(Mockito.anyString()))
                .thenReturn(Mono.just(savedMapping));

        StepVerifier.create(urlShortenerService.getLongUrl(shortUrl))
                .expectNext(savedMapping)
                .verifyComplete();
    }
}