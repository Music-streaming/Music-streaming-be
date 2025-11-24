package com.example.Music_streaming.playlist.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReorderTracksRequest {
    private List<Long> orderedTrackIds;
}
