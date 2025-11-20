package com.example.Music_streaming.playback.dto;

import lombok.Builder;

@Builder
public record PlayableTrackResponse(
        String spotifyTrackId,
        String title,
        String artist,
        String album,
        String thumbnailUrl,
        Integer durationMs,
        String youtubeVideoId,
        String youtubeEmbedUrl,
        String youtubeWatchUrl
) {
}
