package com.example.Music_streaming.spotify;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpotifyAuthService {

    private final SpotifyConfig config;

    public String getAccessToken() {

        String authKey = config.getClientId() + ":" + config.getClientSecret();
        String encodedKey = Base64.getEncoder().encodeToString(authKey.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedKey);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                config.getTokenUrl(),
                HttpMethod.POST,
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }
}
