package com.findDream.controller;

import com.findDream.domain.Comment;
import com.findDream.domain.RatingType;
import com.findDream.dto.CommentRequest;
import com.findDream.dto.CommentResponse;
import com.findDream.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks/{trackId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 📝 댓글 등록
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long trackId,
                                           @RequestBody CommentRequest request,
                                           @AuthenticationPrincipal String userId) {
        // AuthenticationPrincipal에서 받은 userId(String)를 Long으로 변환
        Long currentUserId = Long.parseLong(userId);
        Comment newComment = commentService.addComment(trackId, currentUserId, request);
        return ResponseEntity.ok(new CommentResponse(newComment));
    }

    // 📜 댓글 조회
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long trackId,
                                                             @RequestParam(required = false) RatingType rating) {
        List<CommentResponse> comments = commentService.getComments(trackId, rating);
        return ResponseEntity.ok(comments);
    }

    // ❌ 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long trackId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal String userId) {
        // AuthenticationPrincipal에서 받은 userId(String)를 Long으로 변환
        Long currentUserId = Long.parseLong(userId);
        commentService.deleteComment(commentId, currentUserId);
        return ResponseEntity.ok("삭제 완료");
    }
}
