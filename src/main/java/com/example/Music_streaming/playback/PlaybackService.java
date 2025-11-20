package com.example.Music_streaming.playback;

import com.example.Music_streaming.playback.dto.PlayableTrackResponse;
import com.example.Music_streaming.playback.dto.PlaybackPlaylistResponse;
import com.example.Music_streaming.playlist.Playlist;
import com.example.Music_streaming.playlist.PlaylistRepository;
import com.example.Music_streaming.playlist.PlaylistTrack;
import com.example.Music_streaming.spotify.SpotifyService;
import com.example.Music_streaming.spotify.TrackMetadata;
import com.example.Music_streaming.track.Track;
import com.example.Music_streaming.track.TrackRepository;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.user.UserService;
import com.example.Music_streaming.youtube.YouTubeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaybackService {

    private final SpotifyService spotifyService;
    private final YouTubeService youTubeService;
    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public List<PlayableTrackResponse> searchPlayableTracks(String query) {
        Map<String, Object> spotifyResult = spotifyService.searchTracks(query);
        JsonNode root = objectMapper.convertValue(spotifyResult, JsonNode.class);
        JsonNode items = root.path("tracks").path("items");

        if (!items.isArray() || items.isEmpty()) {
            return List.of();
        }

        List<PlayableTrackResponse> responses = new ArrayList<>();
        for (JsonNode item : items) {
            responses.add(fromSpotifyItem(item));
        }
        return responses;
    }

    public PlayableTrackResponse getPlayableTrack(String spotifyTrackId) {
        TrackMetadata metadata = spotifyService.getTrackMetadata(spotifyTrackId);
        return toPlayableTrackResponse(
                spotifyTrackId,
                metadata.title(),
                metadata.artist(),
                metadata.album(),
                metadata.thumbnailUrl(),
                metadata.durationMs()
        );
    }

    public PlaybackPlaylistResponse getPlaylistQueue(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));

        validateOwner(playlist);

        List<PlayableTrackResponse> tracks = playlist.getTracks().stream()
                .sorted(Comparator.comparingInt(track ->
                        track.getTrackOrder() == null ? Integer.MAX_VALUE : track.getTrackOrder()))
                .map(track -> toPlayableTrackResponse(
                        track.getSpotifyTrackId(),
                        track.getTitle(),
                        track.getArtist(),
                        track.getAlbum(),
                        track.getThumbnailUrl(),
                        track.getDurationMs()
                ))
                .toList();

        return PlaybackPlaylistResponse.builder()
                .playlistId(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .tracks(tracks)
                .build();
    }

    private PlayableTrackResponse fromSpotifyItem(JsonNode item) {
        String spotifyTrackId = item.path("id").asText();
        String title = item.path("name").asText();
        JsonNode artists = item.path("artists");
        String artist = artists.isArray() && !artists.isEmpty()
                ? artists.get(0).path("name").asText()
                : "";
        String album = item.path("album").path("name").asText();
        JsonNode images = item.path("album").path("images");
        String thumbnailUrl = images.isArray() && !images.isEmpty()
                ? images.get(0).path("url").asText()
                : null;
        Integer duration = item.has("duration_ms") ? item.get("duration_ms").asInt() : null;

        return toPlayableTrackResponse(spotifyTrackId, title, artist, album, thumbnailUrl, duration);
    }

    private PlayableTrackResponse toPlayableTrackResponse(String spotifyTrackId,
                                                         String title,
                                                         String artist,
                                                         String album,
                                                         String thumbnailUrl,
                                                         Integer durationMs) {
        String youtubeVideoId = resolveYoutubeVideoId(spotifyTrackId, title, artist, album, thumbnailUrl, durationMs);
        return PlayableTrackResponse.builder()
                .spotifyTrackId(spotifyTrackId)
                .title(title)
                .artist(artist)
                .album(album)
                .thumbnailUrl(thumbnailUrl)
                .durationMs(durationMs)
                .youtubeVideoId(youtubeVideoId)
                .youtubeEmbedUrl(youtubeVideoId != null ? "https://www.youtube.com/embed/" + youtubeVideoId : null)
                .youtubeWatchUrl(youtubeVideoId != null ? "https://www.youtube.com/watch?v=" + youtubeVideoId : null)
                .build();
    }

    private String resolveYoutubeVideoId(String spotifyTrackId,
                                         String title,
                                         String artist,
                                         String album,
                                         String thumbnailUrl,
                                         Integer durationMs) {
        Track track = trackRepository.findBySpotifyTrackId(spotifyTrackId)
                .orElseGet(() -> trackRepository.save(Track.builder()
                        .spotifyTrackId(spotifyTrackId)
                        .title(title)
                        .artist(artist)
                        .album(album)
                        .thumbnailUrl(thumbnailUrl)
                        .durationMs(durationMs)
                        .build()));

        if (track.getYoutubeVideoId() == null || track.getYoutubeVideoId().isBlank()) {
            String query = buildYoutubeQuery(title, artist);
            String videoId = youTubeService.searchVideoId(query);
            track.setYoutubeVideoId(videoId);
            trackRepository.save(track);
            return videoId;
        }

        return track.getYoutubeVideoId();
    }

    private String buildYoutubeQuery(String title, String artist) {
        return (title + " " + (artist != null ? artist : "") + " official audio").trim();
    }

    private void validateOwner(Playlist playlist) {
        User current = userService.getCurrentUser();
        if (!playlist.getOwner().getId().equals(current.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 플레이리스트에 대한 권한이 없습니다.");
        }
    }
}
