package com.findDream.dto;

import com.findDream.domain.Comment;
import com.findDream.domain.RatingType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private String content;
    private String username;
    private RatingType rating;
    private LocalDateTime createdAt;
    private List<CommentResponse> children;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getUser().getUsername();
        this.rating = comment.getRating();
        this.createdAt = comment.getCreatedAt();
        // N+1을 방지하기 위해, children을 여기서 직접 초기화하지 않고 서비스 레이어에서 처리합니다.
    }
}
