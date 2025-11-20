package com.example.Music_streaming.spotify;

import com.example.Music_streaming.spotify.TrackMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
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

    private final WebClient.Builder webClientBuilder;

    private String requestAccessToken() {
        String credentials = clientId + ":" + clientSecret;
        String basicAuth = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> result = webClientBuilder.build()
                .post()
                .uri(tokenUrl)
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (result == null || result.get("access_token") == null) {
            throw new IllegalStateException("Spotify access token을 발급받지 못했습니다.");
        }
        return result.get("access_token").toString();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> searchTracks(String query) {
        String accessToken = requestAccessToken();

        var uri = UriComponentsBuilder.fromHttpUrl(apiUrl + "/search")
                .queryParam("q", query)
                .queryParam("type", "track")
                .queryParam("limit", 10)
                .build(true)
                .toUri();

        return (Map<String, Object>) webClientBuilder.build()
                .get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public TrackMetadata getTrackMetadata(String trackId) {
        String accessToken = requestAccessToken();

        var uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/tracks/{trackId}")
                .buildAndExpand(trackId)
                .toUri();

        JsonNode body = webClientBuilder.build()
                .get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (body == null) {
            throw new IllegalStateException("Spotify 메타데이터 응답이 비어 있습니다.");
        }

        return new TrackMetadata(
                body.get("name").asText(),
                body.get("artists").get(0).get("name").asText(),
                body.get("album").get("name").asText(),
                body.get("album").get("images").get(0).get("url").asText(),
                body.get("duration_ms").asInt()
        );
    }
}
