package com.example.Music_streaming.spotify;

import com.example.Music_streaming.spotify.dto.TrackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spotify")
public class SpotifyController {

    private final SpotifyService spotifyService;

    @GetMapping("/search")
    public List<TrackResponse> search(@RequestParam String query) {
        return spotifyService.searchTracks(query);
    }
}
