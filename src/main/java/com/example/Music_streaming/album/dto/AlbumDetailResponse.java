package com.example.Music_streaming.album.dto;

import java.util.List;

public record AlbumDetailResponse(
        String id,
        String name,
        String artist,
        String thumbnailUrl,
        String releaseDate,
        int totalTracks,
        List<AlbumTrackDto> tracks
) {
}
