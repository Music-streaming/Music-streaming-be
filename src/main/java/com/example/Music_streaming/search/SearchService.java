package com.example.Music_streaming.search;

import com.example.Music_streaming.search.dto.SearchItemDto;
import com.example.Music_streaming.search.dto.SearchResponseDto;
import com.example.Music_streaming.spotify.SpotifyService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SpotifyService spotifyService;

    public SearchResponseDto search(String query) {
        JsonNode node = spotifyService.searchMulti(query, List.of("artist", "album", "track"), 10);
        return new SearchResponseDto(
                mapItems(node.path("artists").path("items"), true),
                mapItems(node.path("albums").path("items"), true),
                mapItems(node.path("tracks").path("items"), false)
        );
    }

    private List<SearchItemDto> mapItems(JsonNode itemsNode, boolean includeTypeSubtitle) {
        List<SearchItemDto> items = new ArrayList<>();
        if (!itemsNode.isArray()) {
            return items;
        }
        itemsNode.forEach(item -> items.add(new SearchItemDto(
                item.path("id").asText(),
                item.path("name").asText(),
                buildSubtitle(item, includeTypeSubtitle),
                extractImage(item)
        )));
        return items;
    }

    private String buildSubtitle(JsonNode item, boolean includeType) {
        if (item.path("artists").isArray() && item.path("artists").size() > 0) {
            return item.path("artists").get(0).path("name").asText();
        }
        if (includeType) {
            return item.path("type").asText();
        }
        return "";
    }

    private String extractImage(JsonNode node) {
        JsonNode images = node.path("images");
        if (images.isArray() && images.size() > 0) {
            return images.get(0).path("url").asText();
        }
        JsonNode album = node.path("album");
        if (album.isObject()) {
            JsonNode albumImages = album.path("images");
            if (albumImages.isArray() && albumImages.size() > 0) {
                return albumImages.get(0).path("url").asText();
            }
        }
        return null;
    }
}
