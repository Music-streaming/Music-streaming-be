package com.example.Music_streaming.track;

import com.example.Music_streaming.like.TrackLikeRepository;
import com.example.Music_streaming.playback.PlaybackService;
import com.example.Music_streaming.playback.dto.PlayableTrackResponse;
import com.example.Music_streaming.playbacklog.PlaybackLog;
import com.example.Music_streaming.playbacklog.PlaybackLogRepository;
import com.example.Music_streaming.track.dto.LyricsResponse;
import com.example.Music_streaming.track.dto.TrackDetailResponse;
import com.example.Music_streaming.track.dto.TrackLyricsRequest;
import com.example.Music_streaming.track.dto.TrackSatisfactionDtos;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.user.UserRepository;
import com.example.Music_streaming.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final PlaybackService playbackService;
    private final TrackRepository trackRepository;
    private final TrackLikeRepository trackLikeRepository;
    private final TrackSatisfactionService trackSatisfactionService;
    private final PlaybackLogRepository playbackLogRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public TrackDetailResponse getTrackDetail(String spotifyTrackId, User currentUser) {
        PlayableTrackResponse playable = playbackService.getPlayableTrack(spotifyTrackId);
        Track track = ensureTrack(spotifyTrackId);

        boolean liked = false;
        if (currentUser != null) {
            liked = trackLikeRepository.existsByTrackAndUser(track, currentUser);
        }

        TrackSatisfactionDtos.SummaryResponse satisfaction = trackSatisfactionService.getSummary(track.getId());
        List<TrackDetailResponse.PlaybackLogEntry> recentLogs = playbackLogRepository.findTop20ByTrackOrderByPlayedAtDesc(track)
                .stream()
                .map(log -> new TrackDetailResponse.PlaybackLogEntry(
                        log.getUser().getUsername(),
                        log.getPlayedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                ))
                .toList();

        return new TrackDetailResponse(
                playable,
                track.getLikeCount(),
                liked,
                satisfaction,
                recentLogs,
                track.getLyrics()
        );
    }

    @Transactional
    public LyricsResponse updateLyrics(String spotifyTrackId, TrackLyricsRequest request) {
        Track track = ensureTrack(spotifyTrackId);
        track.setLyrics(request.lyrics());
        trackRepository.save(track);
        return new LyricsResponse(spotifyTrackId, track.getLyrics());
    }

    @Transactional(readOnly = true)
    public LyricsResponse getLyrics(String spotifyTrackId) {
        Track track = ensureTrack(spotifyTrackId);
        return new LyricsResponse(spotifyTrackId, track.getLyrics());
    }

    @Transactional
    public void logPlayback(String spotifyTrackId) {
        Track track = ensureTrack(spotifyTrackId);
        User user = userService.getCurrentUser();
        PlaybackLog log = new PlaybackLog();
        log.setTrack(track);
        log.setUser(user);
        playbackLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<TrackDetailResponse.PlaybackLogEntry> getPlaybackLogs(String spotifyTrackId) {
        Track track = ensureTrack(spotifyTrackId);
        return playbackLogRepository.findTop20ByTrackOrderByPlayedAtDesc(track)
                .stream()
                .map(log -> new TrackDetailResponse.PlaybackLogEntry(
                        log.getUser().getUsername(),
                        log.getPlayedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                ))
                .toList();
    }

    private Track ensureTrack(String spotifyTrackId) {
        return trackRepository.findBySpotifyTrackId(spotifyTrackId)
                .orElseGet(() -> {
                    playbackService.getPlayableTrack(spotifyTrackId);
                    return trackRepository.findBySpotifyTrackId(spotifyTrackId)
                            .orElseThrow(() -> new IllegalStateException("트랙 정보를 찾을 수 없습니다."));
                });
    }
}
