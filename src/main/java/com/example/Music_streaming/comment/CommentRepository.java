package com.example.Music_streaming.comment;

import com.example.Music_streaming.playlist.PlaylistTrack;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByPlaylistTrackOrderByCreatedAtDesc(PlaylistTrack playlistTrack);
}
