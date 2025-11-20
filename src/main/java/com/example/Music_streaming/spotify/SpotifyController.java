package com.example.Music_streaming.spotify;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spotify")
public class SpotifyController {

    private final SpotifyService spotifyService;

    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String query) {
        return spotifyService.searchTracks(query);
    }
}
