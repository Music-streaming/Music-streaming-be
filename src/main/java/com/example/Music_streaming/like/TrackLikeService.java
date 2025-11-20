package com.example.Music_streaming.like;

import com.example.Music_streaming.track.Track;
import com.example.Music_streaming.track.TrackRepository;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrackLikeService {

    private final TrackLikeRepository likeRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    /**
     * 좋아요 토글 (true = 좋아요됨, false = 좋아요 취소됨)
     */
    @Transactional
    public boolean toggleLike(Long trackId, String userEmail) {

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var exist = likeRepository.findByTrackAndUser(track, user);

        if (exist.isPresent()) {
            likeRepository.delete(exist.get());
            track.setLikeCount(Math.max(0, track.getLikeCount() - 1));
            return false;
        }

        TrackLike like = TrackLike.builder()
                .track(track)
                .user(user)
                .build();

        likeRepository.save(like);
        track.setLikeCount(track.getLikeCount() + 1);

        return true;
    }

    @Transactional(readOnly = true)
    public long count(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));
        return likeRepository.countByTrack(track);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long trackId, String userEmail) {

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return likeRepository.existsByTrackAndUser(track, user);
    }
}
