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
@RequestMapping("/api/spotify-tracks/{spotifyTrackId}/like")
public class SpotifyTrackLikeController {

    private final SpotifyTrackLikeService likeService;

    @PostMapping
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable String spotifyTrackId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = requireUser(principal);
        boolean liked = likeService.toggleLike(spotifyTrackId, email);
        return ResponseEntity.ok(liked);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(@PathVariable String spotifyTrackId) {
        long count = likeService.count(spotifyTrackId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> status(
            @PathVariable String spotifyTrackId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = requireUser(principal);
        boolean liked = likeService.isLiked(spotifyTrackId, email);
        return ResponseEntity.ok(liked);
    }

    @GetMapping("/summary")
    public ResponseEntity<TrackLikeSummaryResponse> summary(
            @PathVariable String spotifyTrackId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = principal != null ? principal.getUsername() : null;
        TrackLikeSummaryResponse summary = likeService.summary(spotifyTrackId, email);
        return ResponseEntity.ok(summary);
    }

    private String requireUser(UserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return principal.getUsername();
    }
}
