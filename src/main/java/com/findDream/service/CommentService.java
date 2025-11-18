package com.findDream.service;

import com.findDream.domain.Comment;
import com.findDream.domain.RatingType;
import com.findDream.domain.Track;
import com.findDream.domain.User;
import com.findDream.dto.CommentRequest;
import com.findDream.dto.CommentResponse;
import com.findDream.repository.CommentRepository;
import com.findDream.repository.TrackRepository;
import com.findDream.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    // 댓글 등록
    @Transactional
    public Comment addComment(Long trackId, Long userId, CommentRequest request) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        Comment comment = Comment.builder()
                .track(track)
                .user(user)
                .content(request.getContent())
                .rating(request.getRating())
                .parent(parent)
                .build();

        return commentRepository.save(comment);
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long trackId, RatingType rating) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        List<Comment> comments;
        if (rating == null) {
            comments = commentRepository.findByTrackOrderByCreatedAtDesc(track);
        } else {
            comments = commentRepository.findByTrackAndRatingOrderByCreatedAtDesc(track, rating);
        }

        Map<Long, CommentResponse> commentResponseMap = new HashMap<>();
        List<CommentResponse> rootComments = new ArrayList<>();

        comments.forEach(comment -> {
            CommentResponse response = new CommentResponse(comment);
            response.setChildren(new ArrayList<>());
            commentResponseMap.put(comment.getId(), response);
        });

        comments.forEach(comment -> {
            if (comment.getParent() != null) {
                CommentResponse parentResponse = commentResponseMap.get(comment.getParent().getId());
                if (parentResponse != null) { // In case parent is not part of the fetched list
                    parentResponse.getChildren().add(commentResponseMap.get(comment.getId()));
                }
            } else {
                rootComments.add(commentResponseMap.get(comment.getId()));
            }
        });

        return rootComments;
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // 본인 댓글인지 확인
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}
