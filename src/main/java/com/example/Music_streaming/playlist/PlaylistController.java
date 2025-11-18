package com.example.Music_streaming.playlist;

import com.example.Music_streaming.playlist.dto.AddTrackRequest;
import com.example.Music_streaming.playlist.dto.CreatePlaylistRequest;
import com.example.Music_streaming.playlist.dto.PlaylistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    // 플레이리스트 생성
    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(@RequestBody CreatePlaylistRequest request) {
        PlaylistResponse response = playlistService.createPlaylist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 내 플레이리스트 목록
    @GetMapping("/me")
    public List<PlaylistResponse> getMyPlaylists() {
        return playlistService.getMyPlaylists();
    }

    // 플레이리스트 상세
    @GetMapping("/{id}")
    public PlaylistResponse getPlaylist(@PathVariable Long id) {
        return playlistService.getPlaylist(id);
    }

    // 트랙 추가
    @PostMapping("/{id}/tracks")
    public PlaylistResponse addTrack(
            @PathVariable Long id,
            @RequestBody AddTrackRequest request
    ) {
        return playlistService.addTrack(id, request);
    }

    // 트랙 삭제
    @DeleteMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<Void> removeTrack(
            @PathVariable Long id,
            @PathVariable Long trackId
    ) {
        playlistService.removeTrack(id, trackId);
        return ResponseEntity.noContent().build();
    }
}
