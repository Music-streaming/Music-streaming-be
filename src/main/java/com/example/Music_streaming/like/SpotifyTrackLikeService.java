package com.example.Music_streaming.like;

import com.example.Music_streaming.user.User;
import com.example.Music_streaming.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpotifyTrackLikeService {

    private final SpotifyTrackLikeRepository likeRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleLike(String spotifyTrackId, String userEmail) {

        if (spotifyTrackId == null || spotifyTrackId.isBlank()) {
            throw new RuntimeException("Spotify 트랙 ID가 필요합니다.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var exist = likeRepository.findBySpotifyTrackIdAndUser(spotifyTrackId, user);

        if (exist.isPresent()) {
            likeRepository.delete(exist.get());
            return false;
        }

        SpotifyTrackLike like = SpotifyTrackLike.builder()
                .spotifyTrackId(spotifyTrackId)
                .user(user)
                .build();

        likeRepository.save(like);
        return true;
    }

    @Transactional(readOnly = true)
    public long count(String spotifyTrackId) {
        if (spotifyTrackId == null || spotifyTrackId.isBlank()) {
            throw new RuntimeException("Spotify 트랙 ID가 필요합니다.");
        }
        return likeRepository.countBySpotifyTrackId(spotifyTrackId);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(String spotifyTrackId, String userEmail) {

        if (spotifyTrackId == null || spotifyTrackId.isBlank()) {
            throw new RuntimeException("Spotify 트랙 ID가 필요합니다.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return likeRepository.existsBySpotifyTrackIdAndUser(spotifyTrackId, user);
    }

    @Transactional(readOnly = true)
    public TrackLikeSummaryResponse summary(String spotifyTrackId, String userEmail) {
        if (spotifyTrackId == null || spotifyTrackId.isBlank()) {
            throw new RuntimeException("Spotify 트랙 ID가 필요합니다.");
        }

        long count = likeRepository.countBySpotifyTrackId(spotifyTrackId);

        if (userEmail == null) {
            return new TrackLikeSummaryResponse(count, null);
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean liked = likeRepository.existsBySpotifyTrackIdAndUser(spotifyTrackId, user);
        return new TrackLikeSummaryResponse(count, liked);
    }
}
