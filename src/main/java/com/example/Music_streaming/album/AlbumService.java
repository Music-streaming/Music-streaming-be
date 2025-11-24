package com.example.Music_streaming.album;

import com.example.Music_streaming.album.dto.AlbumDetailResponse;
import com.example.Music_streaming.album.dto.AlbumTrackDto;
import com.example.Music_streaming.spotify.SpotifyService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final SpotifyService spotifyService;

    public AlbumDetailResponse getAlbum(String albumId) {
        JsonNode albumNode = spotifyService.getAlbum(albumId);
        JsonNode tracksNode = spotifyService.getAlbumTracks(albumId, 50).path("items");
        List<AlbumTrackDto> tracks = new ArrayList<>();
        if (tracksNode.isArray()) {
            tracksNode.forEach(item -> tracks.add(new AlbumTrackDto(
                    item.path("id").asText(),
                    item.path("name").asText(),
                    item.path("track_number").asInt(),
                    item.path("disc_number").asInt(),
                    item.path("duration_ms").asInt()
            )));
        }

        return new AlbumDetailResponse(
                albumNode.path("id").asText(),
                albumNode.path("name").asText(),
                albumNode.path("artists").isArray() && albumNode.path("artists").size() > 0
                        ? albumNode.path("artists").get(0).path("name").asText()
                        : "",
                extractImage(albumNode),
                albumNode.path("release_date").asText(),
                albumNode.path("total_tracks").asInt(),
                tracks
        );
    }

    private String extractImage(JsonNode node) {
        JsonNode images = node.path("images");
        if (images.isArray() && images.size() > 0) {
            return images.get(0).path("url").asText();
        }
        return null;
    }
}
