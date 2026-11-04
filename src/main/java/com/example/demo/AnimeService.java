
package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AnimeService {

    private static final Logger logger =
            LoggerFactory.getLogger(AnimeService.class);

    private final RestTemplate restTemplate;

    public AnimeService() {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);

        this.restTemplate = new RestTemplate(factory);
    }

    public List<Map<String, Object>> searchAnime(String query) {

        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        URI uri = UriComponentsBuilder
                .fromUriString("https://api.tvmaze.com/search/shows")
                .queryParam("q", query.trim())
                .encode()
                .build()
                .toUri();

        try {
            ResponseEntity<List<Map<String, Object>>> response =
                    restTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {}
                    );

            List<Map<String, Object>> results = response.getBody();

            return results != null
                    ? results
                    : Collections.emptyList();

        } catch (Exception e) {
            logger.error("TVmaze search failed", e);
            return Collections.emptyList();
        }
    }

    public Map<String, Object> getShowDetails(Long id) {

        if (id == null || id <= 0) {
            return null;
        }

        try {
            return restTemplate.getForObject(
                    "https://api.tvmaze.com/shows/{id}",
                    Map.class,
                    id
            );

        } catch (Exception e) {
            logger.error("TVmaze details request failed", e);
            return null;
        }
    }
}
