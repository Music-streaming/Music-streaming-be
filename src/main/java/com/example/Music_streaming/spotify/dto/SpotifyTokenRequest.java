package com.example.Music_streaming.spotify.dto;

public record SpotifyTokenRequest(
        String accessToken,
        String refreshToken,
        Long expiresIn
) {
}
