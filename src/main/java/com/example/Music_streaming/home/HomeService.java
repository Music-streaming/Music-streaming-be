package com.example.Music_streaming.home;

import com.example.Music_streaming.home.dto.AlbumSummaryDto;
import com.example.Music_streaming.home.dto.HomeNewReleasesResponse;
import com.example.Music_streaming.home.dto.HomeRecommendationResponse;
import com.example.Music_streaming.home.dto.RecommendedTrackDto;
import com.example.Music_streaming.spotify.SpotifyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final SpotifyService spotifyService;
    private final ObjectMapper objectMapper;

    public HomeRecommendationResponse getRecommendations(List<String> seedTrackIds) {
        List<String> seeds = (seedTrackIds == null || seedTrackIds.isEmpty())
                ? defaultSeedTracks()
                : seedTrackIds;

        JsonNode recommendations = spotifyService.getRecommendedTracks(seeds, 12);
        JsonNode tracksNode = recommendations.path("tracks");

        List<RecommendedTrackDto> tracks = new ArrayList<>();
        if (tracksNode.isArray()) {
            tracksNode.forEach(item -> tracks.add(new RecommendedTrackDto(
                    item.path("id").asText(),
                    item.path("name").asText(),
                    item.path("artists").isArray() && item.path("artists").size() > 0
                            ? item.path("artists").get(0).path("name").asText()
                            : "",
                    item.path("album").path("name").asText(),
                    extractImage(item.path("album")),
                    item.path("duration_ms").isNumber() ? item.path("duration_ms").asInt() : null
            )));
        }

        return new HomeRecommendationResponse(tracks);
    }

    public HomeNewReleasesResponse getNewReleases() {
        JsonNode result = spotifyService.getNewReleases(12);
        JsonNode albumsNode = result.path("albums").path("items");
        List<AlbumSummaryDto> albums = new ArrayList<>();
        if (albumsNode.isArray()) {
            albumsNode.forEach(item -> albums.add(new AlbumSummaryDto(
                    item.path("id").asText(),
                    item.path("name").asText(),
                    item.path("artists").isArray() && item.path("artists").size() > 0
                            ? item.path("artists").get(0).path("name").asText()
                            : "",
                    extractImage(item),
                    item.path("release_date").asText(),
                    item.path("total_tracks").asInt()
            )));
        }
        return new HomeNewReleasesResponse(albums);
    }

    private String extractImage(JsonNode node) {
        JsonNode images = node.path("images");
        if (images.isArray() && images.size() > 0) {
            return images.get(0).path("url").asText();
        }
        return null;
    }

    private List<String> defaultSeedTracks() {
        var searchResult = spotifyService.searchTracks("top hits");
        JsonNode root = objectMapper.convertValue(searchResult, JsonNode.class);
        JsonNode items = root.path("tracks").path("items");
        if (!items.isArray() || items.isEmpty()) {
            return Collections.singletonList("3n3Ppam7vgaVa1iaRUc9Lp");
        }

        List<String> seeds = new ArrayList<>();
        items.forEach(item -> {
            if (seeds.size() < 5) {
                seeds.add(item.path("id").asText());
            }
        });
        return seeds;
    }
}
