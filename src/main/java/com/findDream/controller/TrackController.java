package com.findDream.controller;

import com.findDream.domain.Track;
import com.findDream.domain.User;
import com.findDream.service.TrackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    // 음원 업로드
    @PostMapping
    public ResponseEntity<Track> uploadTrack(@RequestBody Track trackData, HttpSession session) {
        User uploader = (User) session.getAttribute("user");
        if (uploader == null) {
            return ResponseEntity.status(401).build(); // 로그인 안 한 상태
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
    public ResponseEntity<List<Track>> getMyTracks(HttpSession session) {
        User uploader = (User) session.getAttribute("user");
        if (uploader == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(trackService.getTracksByUser(uploader));
    }

    // 트랙 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id, HttpSession session) {
        User uploader = (User) session.getAttribute("user");
        if (uploader == null) {
            return ResponseEntity.status(401).build();
        }

        trackService.deleteTrack(id, uploader);
        return ResponseEntity.noContent().build();
    }
}
