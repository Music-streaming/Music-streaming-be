package com.example.Music_streaming.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PlaylistResponse {

    private Long id;
    private String name;
    private String description;
    private boolean isPublic;

    private List<TrackResponse> tracks;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TrackResponse {
        private Long id;
        private String title;
        private String artist;
        private String album;
        private String thumbnailUrl;
        private Integer durationMs;
        private String spotifyTrackId;
        private Integer order;
    }
}
