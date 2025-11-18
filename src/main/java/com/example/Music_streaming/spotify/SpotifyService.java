package com.example.Music_streaming.spotify;

import com.example.Music_streaming.spotify.dto.TrackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpotifyService {

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.api-url}")
    private String apiUrl;

    @Value("${spotify.api.token.url}")
    private String tokenUrl;

    private final WebClient webClient = WebClient.create();

    // Caching fields from findDream
    private String cachedAccessToken;
    private long tokenExpirationTime = 0;

    // Merged getAccessToken with caching
    private String getAccessToken() {
        long currentTime = System.currentTimeMillis();

        if (cachedAccessToken != null && currentTime < tokenExpirationTime) {
            return cachedAccessToken;
        }

        String basicAuth = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        Map<String, Object> result = webClient.post()
                .uri(tokenUrl) // Use configured tokenUrl
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        cachedAccessToken = (String) result.get("access_token");
        // expires_in is returned as an Integer
        Number expiresInNumber = (Number) result.get("expires_in");
        long expiresIn = expiresInNumber.longValue();
        tokenExpirationTime = currentTime + (expiresIn * 1000L);

        return cachedAccessToken;
    }

    // Merged searchTracks from findDream, adapted for WebClient
    public List<TrackResponse> searchTracks(String query) {
        String accessToken = getAccessToken();

        Map<String, Object> response = webClient.get()
                .uri(apiUrl + "/search?q={query}&type=track&limit=10", query)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // Manual parsing from Map, inspired by findDream's JSONObject logic
        List<TrackResponse> results = new ArrayList<>();
        Map<String, Object> tracks = (Map<String, Object>) response.get("tracks");
        List<Map<String, Object>> items = (List<Map<String, Object>>) tracks.get("items");

        for (Map<String, Object> item : items) {
            String title = (String) item.get("name");
            List<Map<String, Object>> artists = (List<Map<String, Object>>) item.get("artists");
            String artist = "";
            if (artists != null && !artists.isEmpty()) {
                artist = (String) artists.get(0).get("name");
            }

            Map<String, Object> album = (Map<String, Object>) item.get("album");
            List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");
            String albumImage = null;
            if (images != null && !images.isEmpty()) {
                albumImage = (String) images.get(0).get("url");
            }

            String previewUrl = (String) item.get("preview_url");
            Map<String, Object> externalUrls = (Map<String, Object>) item.get("external_urls");
            String spotifyUrl = (String) externalUrls.get("spotify");

            results.add(TrackResponse.builder()
                    .title(title)
                    .artist(artist)
                    .albumImageUrl(albumImage)
                    .previewUrl(previewUrl)
                    .spotifyUrl(spotifyUrl)
                    .build());
        }

        return results;
    }
}