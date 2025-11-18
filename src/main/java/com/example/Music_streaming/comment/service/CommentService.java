package com.example.Music_streaming.comment.service;

import com.example.Music_streaming.comment.domain.Comment;
import com.example.Music_streaming.comment.domain.RatingType;
import com.example.Music_streaming.comment.dto.CommentRequest;
import com.example.Music_streaming.comment.dto.CommentResponse;
import com.example.Music_streaming.comment.repository.CommentRepository;
import com.example.Music_streaming.track.domain.Track;
import com.example.Music_streaming.track.repository.TrackRepository;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    // 댓글 작성
    @Transactional
    public Comment addComment(Long trackId, CommentRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        Comment comment = Comment.builder()
                .track(track)
                .user(user)
                .content(request.getContent())
                .build();

        return commentRepository.save(comment);
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long trackId) {
        return commentRepository.findByTrackId(trackId)
                .stream()
                .map(CommentResponse::new)
                .toList();
    }

    // 댓글 수정
    @Transactional
    public Comment updateComment(Long commentId, CommentRequest request, Long userId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    // 좋아요 / 싫어요
    @Transactional
    public Comment rateComment(Long commentId, Long userId, RatingType ratingType) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (ratingType == RatingType.LIKE) {
            comment.like();
        } else {
            comment.dislike();
        }

        return commentRepository.save(comment);
    }
}
