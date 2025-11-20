package com.example.Music_streaming.playback.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PlaybackPlaylistResponse(
        Long playlistId,
        String name,
        String description,
        List<PlayableTrackResponse> tracks
) {
}
