package com.example.Music_streaming.like;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks/{trackId}/like")
public class TrackLikeController {

    private final TrackLikeService likeService;

    @PostMapping
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable Long trackId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = requireUser(principal);
        boolean liked = likeService.toggleLike(trackId, email);
        return ResponseEntity.ok(liked);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(@PathVariable Long trackId) {
        long count = likeService.count(trackId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> status(
            @PathVariable Long trackId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = requireUser(principal);
        boolean liked = likeService.isLiked(trackId, email);
        return ResponseEntity.ok(liked);
    }

    private String requireUser(UserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return principal.getUsername();
    }
}
