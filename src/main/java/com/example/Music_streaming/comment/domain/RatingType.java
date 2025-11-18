package com.example.Music_streaming.comment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RatingType {
    LIKE("만족"),
    DISLIKE("불만족");

    private final String description;
}
