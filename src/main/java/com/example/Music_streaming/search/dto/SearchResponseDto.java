package com.example.Music_streaming.search.dto;

import java.util.List;

public record SearchResponseDto(
        List<SearchItemDto> artists,
        List<SearchItemDto> albums,
        List<SearchItemDto> tracks
) {
}
