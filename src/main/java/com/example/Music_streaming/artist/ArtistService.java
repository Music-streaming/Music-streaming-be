package com.example.Music_streaming.artist;

import com.example.Music_streaming.artist.dto.ArtistAlbumResponse;
import com.example.Music_streaming.artist.dto.ArtistResponse;
import com.example.Music_streaming.artist.dto.ArtistTopTrackResponse;
import com.example.Music_streaming.spotify.SpotifyService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final SpotifyService spotifyService;

    @Value("${spotify.default.market:US}")
    private String defaultMarket;

    public ArtistResponse getArtist(String artistId) {
        JsonNode node = spotifyService.getArtist(artistId);
        List<String> genres = new ArrayList<>();
        node.path("genres").forEach(g -> genres.add(g.asText()));
        return new ArtistResponse(
                node.path("id").asText(),
                node.path("name").asText(),
                genres.isEmpty() ? "" : genres.get(0),
                node.path("followers").path("total").asInt(),
                extractImage(node),
                genres
        );
    }

    public List<ArtistTopTrackResponse> getTopTracks(String artistId) {
        JsonNode node = spotifyService.getArtistTopTracks(artistId, defaultMarket);
        List<ArtistTopTrackResponse> tracks = new ArrayList<>();
        node.path("tracks").forEach(item -> tracks.add(new ArtistTopTrackResponse(
                item.path("id").asText(),
                item.path("name").asText(),
                item.path("album").path("name").asText(),
                item.path("preview_url").asText(null),
                item.path("duration_ms").asInt()
        )));
        return tracks;
    }

    public List<ArtistAlbumResponse> getAlbums(String artistId) {
        JsonNode node = spotifyService.getArtistAlbums(artistId, 20);
        List<ArtistAlbumResponse> albums = new ArrayList<>();
        node.path("items").forEach(item -> albums.add(new ArtistAlbumResponse(
                item.path("id").asText(),
                item.path("name").asText(),
                item.path("release_date").asText(),
                extractImage(item)
        )));
        return albums;
    }

    private String extractImage(JsonNode node) {
        JsonNode images = node.path("images");
        if (images.isArray() && images.size() > 0) {
            return images.get(0).path("url").asText();
        }
        return null;
    }
}
