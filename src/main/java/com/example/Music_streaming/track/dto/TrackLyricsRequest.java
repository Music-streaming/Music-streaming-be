package com.example.Music_streaming.track.dto;

import jakarta.validation.constraints.NotBlank;

public record TrackLyricsRequest(@NotBlank String lyrics) {
}
