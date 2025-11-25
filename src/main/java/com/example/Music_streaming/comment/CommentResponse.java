package com.example.Music_streaming.comment;

import lombok.Getter;
import lombok.Setter;
import com.example.Music_streaming.track.SatisfactionType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private List<CommentResponse> children;
    private SatisfactionType satisfactionType;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getUser().getUsername();
        this.createdAt = comment.getCreatedAt();
        this.satisfactionType = comment.getSatisfactionType();
    }
}
