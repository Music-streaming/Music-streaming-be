package com.example.Music_streaming.spotify;

public record TrackMetadata(
        String title,
        String artist,
        String album,
        String thumbnailUrl,
        int durationMs
) {}
