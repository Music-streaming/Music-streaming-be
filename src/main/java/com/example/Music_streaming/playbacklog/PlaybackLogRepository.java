package com.example.Music_streaming.playbacklog;

import com.example.Music_streaming.track.Track;
import com.example.Music_streaming.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaybackLogRepository extends JpaRepository<PlaybackLog, Long> {
    List<PlaybackLog> findTop20ByTrackOrderByPlayedAtDesc(Track track);
    List<PlaybackLog> findTop20ByUserOrderByPlayedAtDesc(User user);
}
