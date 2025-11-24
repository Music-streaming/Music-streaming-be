package com.example.Music_streaming.spotify;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.api-url}")
    private String apiUrl;

    @Value("${spotify.api.token.url}")
    private String tokenUrl;

    private final WebClient.Builder webClientBuilder;

    private String requestAccessToken() {
        String credentials = clientId + ":" + clientSecret;
        String basicAuth = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> result = webClientBuilder.build()
                .post()
                .uri(tokenUrl)
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (result == null || result.get("access_token") == null) {
            throw new IllegalStateException("Spotify access token을 발급받지 못했습니다.");
        }
        return result.get("access_token").toString();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> searchTracks(String query) {
        String accessToken = requestAccessToken();

        var uri = UriComponentsBuilder.fromHttpUrl(apiUrl + "/search")
                .queryParam("q", encode(query))
                .queryParam("type", "track")
                .queryParam("limit", 10)
                .build(true)
                .toUri();

        return (Map<String, Object>) webClientBuilder.build()
                .get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public JsonNode searchMulti(String query, List<String> types, int limit) {
        return getSpotifyJson("/search", builder -> builder
                .queryParam("q", encode(query))
                .queryParam("type", String.join(",", types))
                .queryParam("limit", limit));
    }

    public JsonNode getAlbum(String albumId) {
        return getSpotifyJson("/albums/" + albumId, null);
    }

    public JsonNode getAlbumTracks(String albumId, int limit) {
        return getSpotifyJson("/albums/" + albumId + "/tracks", builder -> builder
                .queryParam("limit", limit));
    }

    public JsonNode getArtist(String artistId) {
        return getSpotifyJson("/artists/" + artistId, null);
    }

    public JsonNode getArtistTopTracks(String artistId, String market) {
        return getSpotifyJson("/artists/" + artistId + "/top-tracks", builder -> builder
                .queryParam("market", market));
    }

    public JsonNode getArtistAlbums(String artistId, int limit) {
        return getSpotifyJson("/artists/" + artistId + "/albums", builder -> builder
                .queryParam("include_groups", "album,single")
                .queryParam("limit", limit));
    }

    public JsonNode getNewReleases(int limit) {
        return getSpotifyJson("/browse/new-releases", builder -> builder
                .queryParam("limit", limit));
    }

    public JsonNode getRecommendedTracks(List<String> seedTrackIds, int limit) {
        return getSpotifyJson("/recommendations", builder -> builder
                .queryParam("seed_tracks", String.join(",", seedTrackIds))
                .queryParam("limit", limit));
    }

    public TrackMetadata getTrackMetadata(String trackId) {
        String accessToken = requestAccessToken();

        var uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/tracks/{trackId}")
                .buildAndExpand(trackId)
                .toUri();

        JsonNode body = webClientBuilder.build()
                .get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (body == null) {
            throw new IllegalStateException("Spotify 메타데이터 응답이 비어 있습니다.");
        }

        return new TrackMetadata(
                body.get("name").asText(),
                body.get("artists").get(0).get("name").asText(),
                body.get("album").get("name").asText(),
                body.get("album").get("images").get(0).get("url").asText(),
                body.get("duration_ms").asInt()
        );
    }

    public JsonNode getTrackDetail(String trackId) {
        return getSpotifyJson("/tracks/" + trackId, null);
    }

    private JsonNode getSpotifyJson(String path, Consumer<UriComponentsBuilder> customizer) {
        String accessToken = requestAccessToken();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl + path);
        if (customizer != null) {
            customizer.accept(builder);
        }
        URI uri = builder.build(true).toUri();

        return webClientBuilder.build()
                .get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
