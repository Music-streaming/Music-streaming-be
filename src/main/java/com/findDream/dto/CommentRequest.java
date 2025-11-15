package com.findDream.dto;

import com.findDream.domain.RatingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private String content;
    private RatingType rating;
    private Long parentId;
}
