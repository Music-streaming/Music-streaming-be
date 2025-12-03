package com.example.Music_streaming.genre.dto;

import com.example.Music_streaming.home.dto.RecommendedTrackDto;

import java.util.List;

public record GenreRecommendationResponse(List<RecommendedTrackDto> tracks) {
}
