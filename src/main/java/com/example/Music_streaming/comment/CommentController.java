package com.example.Music_streaming.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/playlist-tracks/{playlistTrackId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long playlistTrackId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = requireUser(principal);
        Comment saved = commentService.addComment(playlistTrackId, email, request);
        return ResponseEntity.ok(new CommentResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long playlistTrackId
    ) {
        return ResponseEntity.ok(commentService.getComments(playlistTrackId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long playlistTrackId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = requireUser(principal);
        commentService.deleteComment(playlistTrackId, commentId, email);
        return ResponseEntity.ok("삭제 완료");
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long playlistTrackId,
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = requireUser(principal);
        return ResponseEntity.ok(commentService.updateComment(playlistTrackId, commentId, email, request));
    }

    private String requireUser(UserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return principal.getUsername();
    }
}
