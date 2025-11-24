package com.example.Music_streaming.home.dto;

public record RecommendedTrackDto(
        String spotifyTrackId,
        String title,
        String artist,
        String album,
        String thumbnailUrl,
        Integer durationMs
) {
}
