package com.example.Music_streaming.user.dto;

public record RegisterRequestDto(
        String email,
        String password,
        String username
) {}
