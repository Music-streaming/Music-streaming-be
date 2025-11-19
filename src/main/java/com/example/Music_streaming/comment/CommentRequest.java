package com.example.Music_streaming.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private String content;
    private Long parentId;   // 대댓글이면 부모 댓글 ID, 아니면 null
}
