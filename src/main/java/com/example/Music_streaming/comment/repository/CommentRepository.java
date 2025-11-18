package com.example.Music_streaming.comment.repository;

import com.example.Music_streaming.comment.domain.Comment;
import com.example.Music_streaming.comment.domain.RatingType;
import com.example.Music_streaming.track.domain.Track;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"user", "children"})
    List<Comment> findByTrackOrderByCreatedAtDesc(Track track);

    @EntityGraph(attributePaths = {"user", "children"})
    List<Comment> findByTrackAndRatingOrderByCreatedAtDesc(Track track, RatingType rating);

    List<Comment> findByTrackId(Long trackId);
}
