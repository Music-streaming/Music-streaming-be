package com.example.Music_streaming.youtube;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class YouTubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.search.url}")
    private String searchUrl;

    @Value("${youtube.api.max-results:1}")
    private int maxResults;

    private final WebClient.Builder webClientBuilder;

    public String searchVideoId(String query) {
        WebClient client = webClientBuilder.build();

        YouTubeSearchResponse response = client.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("www.googleapis.com")
                        .path("/youtube/v3/search")
                        .queryParam("part", "snippet")
                        .queryParam("type", "video")
                        .queryParam("maxResults", maxResults)
                        .queryParam("q", query)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(YouTubeSearchResponse.class)
                .onErrorResume(ex -> Mono.empty())
                .block();

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            return null;
        }

        return response.getItems().get(0).getId().getVideoId();
    }
}
