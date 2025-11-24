package com.example.Music_streaming.artist.dto;

import java.util.List;

public record ArtistResponse(
        String id,
        String name,
        String genre,
        int followers,
        String imageUrl,
        List<String> genres
) {
}
