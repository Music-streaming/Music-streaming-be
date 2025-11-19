package com.example.Music_streaming.like;

import com.example.Music_streaming.track.Track;
import com.example.Music_streaming.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackLikeRepository extends JpaRepository<TrackLike, Long> {

    Optional<TrackLike> findByTrackAndUser(Track track, User user);

    long countByTrack(Track track);

    boolean existsByTrackAndUser(Track track, User user);
}
