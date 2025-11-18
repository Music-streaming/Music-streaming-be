package com.findDream.controller;

import com.findDream.dto.TrackResponse;
import com.findDream.service.SpotifyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    private final SpotifyService spotifyService;

    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/tracks")
    public List<TrackResponse> searchTracks(@RequestParam String query) {
        return spotifyService.searchTracks(query);
    }
}
