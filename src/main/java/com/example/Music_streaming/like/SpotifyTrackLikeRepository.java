package com.example.Music_streaming.like;

import com.example.Music_streaming.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpotifyTrackLikeRepository extends JpaRepository<SpotifyTrackLike, Long> {

    Optional<SpotifyTrackLike> findBySpotifyTrackIdAndUser(String spotifyTrackId, User user);

    long countBySpotifyTrackId(String spotifyTrackId);

    boolean existsBySpotifyTrackIdAndUser(String spotifyTrackId, User user);
}
