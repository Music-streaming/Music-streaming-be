package com.example.Music_streaming.like;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks/{trackId}/like")
public class TrackLikeController {

    private final TrackLikeService likeService;

    // 좋아요 / 취소
    @PostMapping
    public ResponseEntity<?> toggle(
            @PathVariable Long trackId,
            @AuthenticationPrincipal String userId
    ) {
        Long uid = Long.parseLong(userId);

        boolean liked = likeService.toggleLike(trackId, uid);

        return ResponseEntity.ok(
                liked ? "liked" : "unliked"
        );
    }

    // 좋아요 수 조회
    @GetMapping("/count")
    public ResponseEntity<?> count(@PathVariable Long trackId) {
        long count = likeService.count(trackId);
        return ResponseEntity.ok(count);
    }

    // 현재 유저가 좋아요 눌렀는지 여부
    @GetMapping("/status")
    public ResponseEntity<?> status(
            @PathVariable Long trackId,
            @AuthenticationPrincipal String userId
    ) {
        Long uid = Long.parseLong(userId);

        boolean liked = likeService.isLiked(trackId, uid);

        return ResponseEntity.ok(liked);
    }
}
