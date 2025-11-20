package com.example.Music_streaming.track;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {

    Optional<Track> findBySpotifyTrackId(String spotifyTrackId);

    Optional<Track> findByYoutubeVideoId(String youtubeVideoId);
}
