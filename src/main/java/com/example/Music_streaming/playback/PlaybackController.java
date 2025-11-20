package com.example.Music_streaming.playback;

import com.example.Music_streaming.playback.dto.PlayableTrackResponse;
import com.example.Music_streaming.playback.dto.PlaybackPlaylistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlaybackController {

    private final PlaybackService playbackService;

    /**
     * 스포티파이 검색 결과 + 유튜브 매칭 정보를 반환
     */
    @GetMapping("/search")
    public List<PlayableTrackResponse> searchPlayableTracks(@RequestParam String query) {
        return playbackService.searchPlayableTracks(query);
    }

    /**
     * 특정 스포티파이 트랙을 바로 재생 가능한 형태로 반환
     */
    @GetMapping("/tracks/{spotifyTrackId}")
    public PlayableTrackResponse getPlayableTrack(@PathVariable String spotifyTrackId) {
        return playbackService.getPlayableTrack(spotifyTrackId);
    }

    /**
     * 플레이리스트를 재생 큐 형태로 내려줌
     */
    @GetMapping("/playlists/{playlistId}")
    public PlaybackPlaylistResponse getPlaylistQueue(@PathVariable Long playlistId) {
        return playbackService.getPlaylistQueue(playlistId);
    }
}
