package com.example.Music_streaming.playlist.dto;

import com.example.Music_streaming.playlist.Playlist;
import com.example.Music_streaming.playlist.PlaylistTrack;
import lombok.*;

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

    public static PlaylistResponse from(Playlist playlist) {
        List<PlaylistTrack> trackList = playlist.getTracks() == null
                ? List.of()
                : playlist.getTracks();

        return PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .isPublic(playlist.isPublic())
                .tracks(
                        trackList.stream()
                                .map(TrackResponse::from)
                                .toList()
                )
                .build();
    }

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

        public static TrackResponse from(PlaylistTrack t) {
            return TrackResponse.builder()
                    .id(t.getId())
                    .title(t.getTitle())
                    .artist(t.getArtist())
                    .album(t.getAlbum())
                    .thumbnailUrl(t.getThumbnailUrl())
                    .durationMs(t.getDurationMs())
                    .spotifyTrackId(t.getSpotifyTrackId())
                    .order(t.getTrackOrder())
                    .build();
        }
    }
}
