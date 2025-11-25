package com.example.Music_streaming.like;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrackLikeSummaryResponse {
    private final long count;
    private final Boolean liked; // null이면 로그인 안 한 사용자
}
