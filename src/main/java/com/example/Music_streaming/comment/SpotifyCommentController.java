package com.example.Music_streaming.comment;

import com.example.Music_streaming.track.SatisfactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spotify-tracks/{spotifyTrackId}/comments")
public class SpotifyCommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String spotifyTrackId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = requireUser(principal);
        Comment saved = commentService.addCommentForSpotify(spotifyTrackId, email, request);
        return ResponseEntity.ok(new CommentResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable String spotifyTrackId,
            @RequestParam(required = false, name = "satisfactionType") SatisfactionType satisfactionType
    ) {
        return ResponseEntity.ok(commentService.getCommentsBySpotify(spotifyTrackId, satisfactionType));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable String spotifyTrackId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = requireUser(principal);
        commentService.deleteCommentBySpotify(spotifyTrackId, commentId, email);
        return ResponseEntity.ok("삭제 완료");
    }

    private String requireUser(UserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return principal.getUsername();
    }
}
