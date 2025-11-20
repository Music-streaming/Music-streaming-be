package com.example.Music_streaming.playlist;

import com.example.Music_streaming.playlist.dto.AddTrackRequest;
import com.example.Music_streaming.playlist.dto.CreatePlaylistRequest;
import com.example.Music_streaming.playlist.dto.PlaylistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(@RequestBody CreatePlaylistRequest request) {
        PlaylistResponse response = playlistService.createPlaylist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public List<PlaylistResponse> getMyPlaylists() {
        return playlistService.getMyPlaylists();
    }

    @GetMapping("/{id}")
    public PlaylistResponse getPlaylist(@PathVariable Long id) {
        return playlistService.getPlaylist(id);
    }

    @PostMapping("/{id}/tracks")
    public PlaylistResponse addTrack(@PathVariable Long id,
                                     @RequestBody AddTrackRequest request) {
        return playlistService.addTrack(id, request);
    }

    @DeleteMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<Void> removeTrack(@PathVariable Long id,
                                            @PathVariable Long trackId) {
        playlistService.removeTrack(id, trackId);
        return ResponseEntity.noContent().build();
    }
}
