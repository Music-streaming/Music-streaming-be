package com.example.Music_streaming.comment;

import com.example.Music_streaming.playlist.PlaylistTrack;
import com.example.Music_streaming.playlist.PlaylistTrackRepository;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PlaylistTrackRepository playlistTrackRepository;

    @Transactional
    public Comment addComment(Long playlistTrackId, String userEmail, CommentRequest req) {

        PlaylistTrack track = playlistTrackRepository.findById(playlistTrackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepository.findById(req.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        Comment comment = Comment.builder()
                .playlistTrack(track)
                .user(user)
                .content(req.getContent())
                .parent(parent)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long playlistTrackId) {

        PlaylistTrack track = playlistTrackRepository.findById(playlistTrackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        List<Comment> comments = commentRepository.findByPlaylistTrackOrderByCreatedAtDesc(track);

        Map<Long, CommentResponse> map = new HashMap<>();
        List<CommentResponse> roots = new ArrayList<>();

        comments.forEach(c -> {
            CommentResponse r = new CommentResponse(c);
            r.setChildren(new ArrayList<>());
            map.put(c.getId(), r);
        });

        comments.forEach(c -> {
            if (c.getParent() != null) {
                CommentResponse parent = map.get(c.getParent().getId());
                if (parent != null) {
                    parent.getChildren().add(map.get(c.getId()));
                }
            } else {
                roots.add(map.get(c.getId()));
            }
        });

        return roots;
    }

    @Transactional
    public void deleteComment(Long commentId, String userEmail) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}
