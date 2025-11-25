package com.example.Music_streaming.comment;

import lombok.Getter;
import lombok.Setter;
import com.example.Music_streaming.track.SatisfactionType;

@Getter
@Setter
public class CommentRequest {
    private String content;
    private Long parentId;   // 대댓글이면 부모 댓글 ID, 아니면 null
    private SatisfactionType satisfactionType; // 만족/불만족 여부 필터를 위한 정보
}
