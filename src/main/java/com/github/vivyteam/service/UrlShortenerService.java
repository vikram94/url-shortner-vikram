package com.github.vivyteam.service;

import com.github.vivyteam.dto.UrlMapping;
import com.github.vivyteam.repository.UrlMappingRepository;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.github.vivyteam.Constants.UrlShortnerConstants.BASE;
import static com.github.vivyteam.Constants.UrlShortnerConstants.BASE_CHARACTERS;

@Service
public class UrlShortenerService {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);
    @Value("${shorturl.baseurl}")
    private String baseUrl;
    @Autowired
    private UrlMappingRepository urlMappingRepository;

    public Mono<String> shortenUrl(UrlMapping urlMapping) {
        UrlValidator urlValidator = new UrlValidator();
        if(!urlValidator.isValid(urlMapping.getLongUrl()))
            return Mono.just("Invalid input url");
        return urlMappingRepository.findByLongUrl(urlMapping.getLongUrl())
                .switchIfEmpty(urlMappingRepository.save(new UrlMapping(null, urlMapping.getLongUrl(), null)))
                .map(savedUrlMapping -> {
                    if (savedUrlMapping.getShortUrl() == null) {
                        String shortUrl = encodeId(savedUrlMapping.getId());
                        savedUrlMapping.setShortUrl(shortUrl);
                        return savedUrlMapping;
                    }
                    logger.info("short url {} already exist for given long url {}"
                            ,savedUrlMapping.getShortUrl(), urlMapping.getLongUrl());
                    return savedUrlMapping;
                })
                .flatMap(urlMappingRepository::save)
                .map(savedUrlMapping -> baseUrl+ savedUrlMapping.getShortUrl());
    }

    public Mono<UrlMapping> getLongUrl(String shortUrl) {
        logger.info("getting long url for given short url: {}", shortUrl);
        return urlMappingRepository.findByShortUrl(shortUrl);
    }

    private String encodeId(Long id) {
        StringBuilder shortURL = new StringBuilder();
        while (id > 0) {
            int remainder = (int) (id % BASE);
            shortURL.insert(0, BASE_CHARACTERS.charAt(remainder));
            id /= BASE;
        }
        logger.info("unique Short URL id: {}", shortURL);
        return shortURL.toString();
    }
}
