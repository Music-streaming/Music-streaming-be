package com.findDream.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackResponse {
    private String title;
    private String artist;
    private String albumImageUrl;
    private String previewUrl;
    private String spotifyUrl;
}
