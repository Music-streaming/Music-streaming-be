package com.example.Music_streaming.home.dto;

public record AlbumSummaryDto(
        String albumId,
        String name,
        String artist,
        String thumbnailUrl,
        String releaseDate,
        int totalTracks
) {
}
