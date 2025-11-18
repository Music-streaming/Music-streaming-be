package com.example.Music_streaming.spotify;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SpotifySearchService {

    private final SpotifyAuthService authService;
    private final SpotifyConfig config;

    public String searchTrack(String query) {
        String accessToken = authService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        RestTemplate rest = new RestTemplate();
        String url = config.getApiBaseUrl() + "/search?q=" + query + "&type=track";

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
