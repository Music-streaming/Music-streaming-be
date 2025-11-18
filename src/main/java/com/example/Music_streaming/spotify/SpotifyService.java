package com.example.Music_streaming.spotify;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.api-url}")
    private String apiUrl;

    @Value("${spotify.api.token.url}")
    private String tokenUrl;

    private final WebClient webClient = WebClient.create();

    // ✔ Access Token 발급
    private String getAccessToken() {

        String basicAuth = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        Map<String, Object> result = webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return result.get("access_token").toString();
    }

    // ✔ 곡 검색
    public Map searchTracks(String query) {

        String accessToken = getAccessToken();

        return webClient.get()
                .uri(apiUrl + "/search?q={query}&type=track&limit=10", query)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
