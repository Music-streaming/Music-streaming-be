package com.example.Music_streaming.track.repository;

import com.example.Music_streaming.track.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    // Find tracks by the uploader's email, assuming the User entity has an 'email' field
    List<Track> findByUploader_Email(String email);
}
