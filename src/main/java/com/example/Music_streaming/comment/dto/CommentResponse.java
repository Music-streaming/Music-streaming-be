package com.example.Music_streaming.comment.dto;

import com.example.Music_streaming.comment.domain.Comment;
import com.example.Music_streaming.comment.domain.RatingType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
        // Service layer will handle populating children
    }
}
