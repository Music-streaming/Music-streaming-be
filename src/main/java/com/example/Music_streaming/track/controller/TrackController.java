package com.example.Music_streaming.track.controller;

import com.example.Music_streaming.track.domain.Track;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.track.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    // 음원 업로드
    @PostMapping
    public ResponseEntity<Track> uploadTrack(@RequestBody Track trackData, @AuthenticationPrincipal User uploader) {
        // The 'uploader' is now injected by Spring Security
        if (uploader == null) {
            // This case should be handled by SecurityConfig, but as a fallback
            return ResponseEntity.status(401).build();
        }

        Track savedTrack = trackService.uploadTrack(
                trackData.getTitle(),
                trackData.getArtist(),
                trackData.getUrl(),
                uploader
        );
        return ResponseEntity.ok(savedTrack);
    }

    // 전체 트랙 조회
    @GetMapping
    public List<Track> getAllTracks() {
        return trackService.getAllTracks();
    }

    // 특정 유저의 트랙 목록
    @GetMapping("/my")
    public ResponseEntity<List<Track>> getMyTracks(@AuthenticationPrincipal User uploader) {
        if (uploader == null) {
            return ResponseEntity.status(401).build();
        }
        // The service now expects an email
        return ResponseEntity.ok(trackService.getTracksByUserEmail(uploader.getEmail()));
    }

    // 트랙 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id, @AuthenticationPrincipal User uploader) {
        if (uploader == null) {
            return ResponseEntity.status(401).build();
        }

        trackService.deleteTrack(id, uploader);
        return ResponseEntity.noContent().build();
    }
}
