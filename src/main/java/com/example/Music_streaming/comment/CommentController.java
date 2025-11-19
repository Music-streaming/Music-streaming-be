package com.example.Music_streaming.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlist-tracks/{playlistTrackId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long playlistTrackId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = principal.getUsername(); // JwtTokenProvider에서 setSubject(email) 했으니까
        Comment saved = commentService.addComment(playlistTrackId, email, request);
        return ResponseEntity.ok(new CommentResponse(saved));
    }

    // 댓글 조회
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long playlistTrackId
    ) {
        return ResponseEntity.ok(commentService.getComments(playlistTrackId));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long playlistTrackId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = principal.getUsername();
        commentService.deleteComment(commentId, email);
        return ResponseEntity.ok("삭제 완료");
    }
}
