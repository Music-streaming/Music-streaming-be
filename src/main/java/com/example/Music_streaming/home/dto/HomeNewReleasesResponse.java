package com.example.Music_streaming.home.dto;

import java.util.List;

public record HomeNewReleasesResponse(
        List<AlbumSummaryDto> albums
) {
}
