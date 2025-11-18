package com.example.Music_streaming.spotify;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpotifyConfig {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    @Value("${spotify.api.token.url}")
    private String tokenUrl;

    @Value("${spotify.api.base.url}")
    private String apiBaseUrl;
}
