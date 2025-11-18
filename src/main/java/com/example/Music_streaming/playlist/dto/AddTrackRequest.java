package com.example.Music_streaming.playlist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTrackRequest {

    private String title;
    private String artist;
    private String album;
    private String thumbnailUrl;
    private Integer durationMs;
    private String spotifyTrackId;
    private Integer order;   // 플레이리스트 안에서의 순서
}
