package com.example.Music_streaming.track;

import com.example.Music_streaming.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackSatisfactionRepository extends JpaRepository<TrackSatisfaction, Long> {

    Optional<TrackSatisfaction> findByTrackAndUser(Track track, User user);

    long countByTrackAndType(Track track, SatisfactionType type);
}
