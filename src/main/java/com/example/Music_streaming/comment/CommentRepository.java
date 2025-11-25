package com.example.Music_streaming.comment;

import com.example.Music_streaming.playlist.PlaylistTrack;
import com.example.Music_streaming.track.SatisfactionType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByPlaylistTrackOrderByCreatedAtDesc(PlaylistTrack playlistTrack);

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByPlaylistTrackAndSatisfactionTypeOrderByCreatedAtDesc(
            PlaylistTrack playlistTrack,
            SatisfactionType satisfactionType
    );

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findBySpotifyTrackIdOrderByCreatedAtDesc(String spotifyTrackId);

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findBySpotifyTrackIdAndSatisfactionTypeOrderByCreatedAtDesc(
            String spotifyTrackId,
            SatisfactionType satisfactionType
    );
}
