package com.example.Music_streaming.playlist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePlaylistRequest {
    private String name;
    private String description;
    private Boolean isPublic;
}
