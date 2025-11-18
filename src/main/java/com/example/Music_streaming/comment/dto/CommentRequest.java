package com.example.Music_streaming.comment.dto;

import com.example.Music_streaming.comment.domain.RatingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private String content;
    private RatingType rating;
    private Long parentId;
}
