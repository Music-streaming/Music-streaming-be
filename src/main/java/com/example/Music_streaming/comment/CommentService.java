package com.example.Music_streaming.comment;

import com.example.Music_streaming.playlist.PlaylistTrack;
import com.example.Music_streaming.playlist.PlaylistTrackRepository;
import com.example.Music_streaming.track.SatisfactionType;
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

    // ----------------------- PlaylistTrack 댓글 -----------------------
    @Transactional
    public Comment addComment(Long playlistTrackId, String userEmail, CommentRequest req) {

        PlaylistTrack track = playlistTrackRepository.findById(playlistTrackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // parentId 처리
        Comment parent = null;
        Long parentId = req.getParentId();

        if (parentId != null && parentId != 0) {
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));

            if (parent.getPlaylistTrack() == null ||
                    !parent.getPlaylistTrack().getId().equals(playlistTrackId)) {
                throw new RuntimeException("부모 댓글이 다른 트랙에 속해 있습니다.");
            }
        }

        Comment comment = Comment.builder()
                .playlistTrack(track)
                .user(user)
                .content(req.getContent())
                .parent(parent)
                .satisfactionType(req.getSatisfactionType())
                .build();

        return commentRepository.save(comment);
    }


    // ----------------------- SpotifyTrack 댓글 -----------------------
    @Transactional
    public Comment addCommentForSpotify(String spotifyTrackId, String userEmail, CommentRequest req) {

        if (spotifyTrackId == null || spotifyTrackId.isBlank()) {
            throw new RuntimeException("Spotify 트랙 ID가 필요합니다.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // parentId 처리
        Comment parent = null;
        Long parentId = req.getParentId();

        if (parentId != null && parentId != 0) {
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));

            if (parent.getPlaylistTrack() != null ||
                    !spotifyTrackId.equals(parent.getSpotifyTrackId())) {
                throw new RuntimeException("부모 댓글이 다른 트랙에 속해 있습니다.");
            }
        }

        Comment comment = Comment.builder()
                .playlistTrack(null)
                .spotifyTrackId(spotifyTrackId)
                .user(user)
                .content(req.getContent())
                .parent(parent)
                .satisfactionType(req.getSatisfactionType())
                .build();

        return commentRepository.save(comment);
    }


    // ----------------------- 댓글 조회 -----------------------
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long playlistTrackId, SatisfactionType satisfactionType) {

        PlaylistTrack track = playlistTrackRepository.findById(playlistTrackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        List<Comment> comments = (satisfactionType == null)
                ? commentRepository.findByPlaylistTrackOrderByCreatedAtDesc(track)
                : commentRepository.findByPlaylistTrackAndSatisfactionTypeOrderByCreatedAtDesc(track, satisfactionType);

        return buildTree(comments);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsBySpotify(String spotifyTrackId, SatisfactionType satisfactionType) {

        if (spotifyTrackId == null || spotifyTrackId.isBlank()) {
            throw new RuntimeException("Spotify 트랙 ID가 필요합니다.");
        }

        List<Comment> comments = (satisfactionType == null)
                ? commentRepository.findBySpotifyTrackIdOrderByCreatedAtDesc(spotifyTrackId)
                : commentRepository.findBySpotifyTrackIdAndSatisfactionTypeOrderByCreatedAtDesc(spotifyTrackId, satisfactionType);

        return buildTree(comments);
    }


    // ----------------------- 트리 빌드 -----------------------
    private List<CommentResponse> buildTree(List<Comment> comments) {
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
                if (parent != null) parent.getChildren().add(map.get(c.getId()));
                else roots.add(map.get(c.getId()));
            } else {
                roots.add(map.get(c.getId()));
            }
        });

        return roots;
    }


    // ----------------------- 삭제 -----------------------
    @Transactional
    public void deleteComment(Long playlistTrackId, Long commentId, String userEmail) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (comment.getPlaylistTrack() == null ||
                !comment.getPlaylistTrack().getId().equals(playlistTrackId)) {
            throw new RuntimeException("잘못된 트랙의 댓글입니다.");
        }

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }


    @Transactional
    public void deleteCommentBySpotify(String spotifyTrackId, Long commentId, String userEmail) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (comment.getPlaylistTrack() != null ||
                !spotifyTrackId.equals(comment.getSpotifyTrackId())) {
            throw new RuntimeException("잘못된 트랙의 댓글입니다.");
        }

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}
