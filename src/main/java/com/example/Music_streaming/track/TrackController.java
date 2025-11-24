package com.example.Music_streaming.track;

import com.example.Music_streaming.track.dto.LyricsResponse;
import com.example.Music_streaming.track.dto.TrackDetailResponse;
import com.example.Music_streaming.track.dto.TrackLyricsRequest;
import com.example.Music_streaming.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;
    private final UserRepository userRepository;

    @GetMapping("/{spotifyTrackId}")
    public TrackDetailResponse getTrack(
            @PathVariable String spotifyTrackId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        var user = principal == null ? null : userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return trackService.getTrackDetail(spotifyTrackId, user);
    }

    @GetMapping("/{spotifyTrackId}/lyrics")
    public LyricsResponse getLyrics(@PathVariable String spotifyTrackId) {
        return trackService.getLyrics(spotifyTrackId);
    }

    @PutMapping("/{spotifyTrackId}/lyrics")
    public LyricsResponse updateLyrics(
            @PathVariable String spotifyTrackId,
            @RequestBody TrackLyricsRequest request
    ) {
        return trackService.updateLyrics(spotifyTrackId, request);
    }

    @PostMapping("/{spotifyTrackId}/playbacks")
    @ResponseStatus(HttpStatus.CREATED)
    public void logPlayback(@PathVariable String spotifyTrackId) {
        trackService.logPlayback(spotifyTrackId);
    }

    @GetMapping("/{spotifyTrackId}/playbacks")
    public List<TrackDetailResponse.PlaybackLogEntry> playbackLogs(@PathVariable String spotifyTrackId) {
        return trackService.getPlaybackLogs(spotifyTrackId);
    }
}
