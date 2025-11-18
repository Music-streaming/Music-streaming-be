package com.example.Music_streaming.playlist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePlaylistRequest {

    private String name;
    private String description;
    private boolean isPublic;
}
