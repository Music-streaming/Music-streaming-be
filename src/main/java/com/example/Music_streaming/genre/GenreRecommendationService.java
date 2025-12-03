package com.example.Music_streaming.genre;

import com.example.Music_streaming.home.dto.RecommendedTrackDto;
import com.example.Music_streaming.spotify.SpotifyService;
import com.example.Music_streaming.user.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreRecommendationService {

    private static final int DEFAULT_LIMIT = 12;
    private final SpotifyService spotifyService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public List<String> getAvailableGenres() {
        String accessToken = userService.getCurrentUserSpotifyAccessToken();
        return spotifyService.getAvailableGenreSeeds(accessToken);
    }

    public List<RecommendedTrackDto> getRecommendations(List<String> genres, Integer limit) {
        if (genres == null || genres.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "최소 한 개 이상의 장르를 입력하세요.");
        }
        int size = limit == null ? DEFAULT_LIMIT : Math.max(1, Math.min(limit, 50));

        try {
            String accessToken = userService.getCurrentUserSpotifyAccessToken();
            JsonNode node = spotifyService.getRecommendationsByGenres(genres, size, accessToken);
            return mapTracks(node.path("tracks"), size);
        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Spotify genre recommendations returned 404. Falling back to search results.");
                return fallbackBySearch(genres, size);
            }
            throw new ResponseStatusException(BAD_GATEWAY, "Spotify 추천 API 호출에 실패했습니다.");
        }
    }

    private String extractImage(JsonNode albumNode) {
        JsonNode images = albumNode.path("images");
        if (images.isArray() && images.size() > 0) {
            return images.get(0).path("url").asText();
        }
        return null;
    }

    private List<RecommendedTrackDto> mapTracks(JsonNode tracks, int maxSize) {
        List<RecommendedTrackDto> results = new ArrayList<>();
        if (!tracks.isArray()) {
            return results;
        }
        tracks.forEach(item -> {
            if (results.size() < maxSize) {
                results.add(mapTrack(item));
            }
        });
        return results;
    }

    private RecommendedTrackDto mapTrack(JsonNode item) {
        return new RecommendedTrackDto(
                item.path("id").asText(),
                item.path("name").asText(),
                item.path("artists").isArray() && item.path("artists").size() > 0
                        ? item.path("artists").get(0).path("name").asText()
                        : "",
                item.path("album").path("name").asText(),
                extractImage(item.path("album")),
                item.path("duration_ms").isNumber() ? item.path("duration_ms").asInt() : null
        );
    }

    private List<RecommendedTrackDto> fallbackBySearch(List<String> genres, int maxSize) {
        List<RecommendedTrackDto> results = new ArrayList<>();
        for (String genre : genres) {
            if (results.size() >= maxSize) {
                break;
            }
            var searchResult = spotifyService.searchTracks(genre + " hits");
            JsonNode root = objectMapper.convertValue(searchResult, JsonNode.class);
            JsonNode items = root.path("tracks").path("items");
            if (!items.isArray()) {
                continue;
            }
            for (JsonNode item : items) {
                results.add(mapTrack(item));
                if (results.size() >= maxSize) {
                    break;
                }
            }
        }

        if (results.isEmpty()) {
            var fallbackSearch = spotifyService.searchTracks("top hits");
            JsonNode root = objectMapper.convertValue(fallbackSearch, JsonNode.class);
            JsonNode items = root.path("tracks").path("items");
            results.addAll(mapTracks(items, maxSize));
        }

        return results;
    }
}
