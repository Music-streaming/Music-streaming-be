package com.example.Music_streaming.track.dto;

import com.example.Music_streaming.playback.dto.PlayableTrackResponse;

import java.util.List;

public record TrackDetailResponse(
        PlayableTrackResponse playable,
        long likeCount,
        boolean liked,
        TrackSatisfactionDtos.SummaryResponse satisfaction,
        List<PlaybackLogEntry> recentPlaybacks,
        String lyrics
) {
    public record PlaybackLogEntry(String username, String playedAt) {}
}
