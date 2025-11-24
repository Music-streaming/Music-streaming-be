package com.example.Music_streaming.album.dto;

public record AlbumTrackDto(
        String id,
        String name,
        int trackNumber,
        int discNumber,
        int durationMs
) {
}
