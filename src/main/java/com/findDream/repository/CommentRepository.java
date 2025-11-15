package com.findDream.repository;

import com.findDream.domain.Comment;
import com.findDream.domain.RatingType;
import com.findDream.domain.Track;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByTrackOrderByCreatedAtDesc(Track track);

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByTrackAndRatingOrderByCreatedAtDesc(Track track, RatingType rating);
}
