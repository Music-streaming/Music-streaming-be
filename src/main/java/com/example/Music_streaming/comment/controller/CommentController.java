package com.example.Music_streaming.comment.controller;

import com.example.Music_streaming.comment.domain.Comment;
import com.example.Music_streaming.comment.domain.RatingType;
import com.example.Music_streaming.comment.dto.CommentRequest;
import com.example.Music_streaming.comment.dto.CommentResponse;
import com.example.Music_streaming.comment.service.CommentService;
import com.example.Music_streaming.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{trackId}")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long trackId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {

        if (user == null) return ResponseEntity.status(401).build();

        Comment comment = commentService.addComment(trackId, request, user.getId());
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    // 댓글 조회
    @GetMapping("/{trackId}")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long trackId) {
        return ResponseEntity.ok(commentService.getComments(trackId));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {

        if (user == null) return ResponseEntity.status(401).build();

        Comment updated = commentService.updateComment(commentId, request, user.getId());
        return ResponseEntity.ok(new CommentResponse(updated));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user) {

        if (user == null) return ResponseEntity.status(401).build();

        commentService.deleteComment(commentId, user.getId());
        return ResponseEntity.ok("삭제 완료");
    }

    // 좋아요
    @PostMapping("/{commentId}/like")
    public ResponseEntity<CommentResponse> likeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user) {

        if (user == null) return ResponseEntity.status(401).build();

        Comment comment = commentService.rateComment(commentId, user.getId(), RatingType.LIKE);
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    // 싫어요
    @PostMapping("/{commentId}/dislike")
    public ResponseEntity<CommentResponse> dislikeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user) {

        if (user == null) return ResponseEntity.status(401).build();

        Comment comment = commentService.rateComment(commentId, user.getId(), RatingType.DISLIKE);
        return ResponseEntity.ok(new CommentResponse(comment));
    }
}
