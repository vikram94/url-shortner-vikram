package com.github.vivyteam.controller;

import com.github.vivyteam.dto.UrlMapping;
import com.github.vivyteam.service.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    public Mono<String> shortenUrl(@RequestBody UrlMapping urlMapping) {
        return urlShortenerService.shortenUrl(urlMapping);
    }

    @GetMapping("/expand/{shortUrl}")
    public Mono<String> expandUrl(@PathVariable String shortUrl) {
        return urlShortenerService.getLongUrl(shortUrl)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found"))))
            .map(UrlMapping::getLongUrl);
    }

    @GetMapping("/{shortUrl}")
    public Mono<Void> redirect(@PathVariable String shortUrl, ServerHttpResponse response) {
        return urlShortenerService.getLongUrl(shortUrl)
           .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found"))))
           .flatMap(urlMapping -> {
               String longUrl = urlMapping.getLongUrl();
               response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
               response.getHeaders().setLocation(URI.create(longUrl));
               return response.setComplete();
           });
    }
}
