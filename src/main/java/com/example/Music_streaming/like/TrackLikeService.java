package com.example.Music_streaming.like;

import com.example.Music_streaming.track.Track;
import com.example.Music_streaming.track.TrackRepository;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackLikeService {

    private final TrackLikeRepository likeRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    public boolean toggleLike(Long trackId, Long userId) {

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 이미 좋아요 눌렀으면 취소
        var exist = likeRepository.findByTrackAndUser(track, user);

        if (exist.isPresent()) {
            likeRepository.delete(exist.get());
            track.setLikeCount(track.getLikeCount() - 1);
            trackRepository.save(track);
            return false; // 좋아요 취소됨
        }

        // 처음 좋아요
        TrackLike like = TrackLike.builder()
                .track(track)
                .user(user)
                .build();

        likeRepository.save(like);

        track.setLikeCount(track.getLikeCount() + 1);
        trackRepository.save(track);

        return true; // 좋아요 성공
    }

    public long count(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        return likeRepository.countByTrack(track);
    }

    public boolean isLiked(Long trackId, Long userId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return likeRepository.existsByTrackAndUser(track, user);
    }
}
