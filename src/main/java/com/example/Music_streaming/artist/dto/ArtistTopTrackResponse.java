package com.example.Music_streaming.artist.dto;

public record ArtistTopTrackResponse(
        String id,
        String name,
        String album,
        String previewUrl,
        int durationMs
) {
}
