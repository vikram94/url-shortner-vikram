package com.github.vivyteam.controller;

import com.github.vivyteam.dto.UrlMapping;
import com.github.vivyteam.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = UrlShortenerController.class)
class UrlShortenerControllerTest {

    @Spy
    @InjectMocks
    private UrlShortenerController urlShortenerController;
    @MockBean
    private UrlShortenerService urlShortenerService;

    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void testShortenUrl() {
        String longUrl = "http://example.com";
        String shortUrl = "b";
        UrlMapping savedMapping = new UrlMapping(null, longUrl, null);
        when(urlShortenerService.shortenUrl(savedMapping))
                .thenReturn(Mono.just(shortUrl));

        webTestClient.post().uri("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(savedMapping)
                .exchange()
                .expectBody(String.class).isEqualTo(shortUrl);
    }

    @Test
    void testExpandUrl() {
        String shortUrl = "abc123";
        String longUrl = "https://github.com/VivyTeam/url-shortener-be-test";
        UrlMapping savedMapping = new UrlMapping(1L, longUrl, shortUrl);

        when(urlShortenerService.getLongUrl(Mockito.anyString()))
                .thenReturn(Mono.just(savedMapping));

        webTestClient.get().uri("/expand/{shortUrl}", shortUrl)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(longUrl);
    }

    @Test
    public void testRedirect() {
        String shortUrl = "your-short-url";
        String longUrl = "https://www.example.com/long-url";
        UrlMapping savedMapping = new UrlMapping(1L, longUrl, shortUrl);
        when(urlShortenerService.getLongUrl(shortUrl)).thenReturn(Mono.just(savedMapping));

        webTestClient.get().uri("/{shortUrl}", shortUrl)
                .exchange()
                .expectStatus().isTemporaryRedirect()
                .expectHeader().valueEquals("Location", longUrl);
    }
}