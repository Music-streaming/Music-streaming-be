package com.example.Music_streaming.spotify;

import com.example.Music_streaming.spotify.dto.SpotifyTokenRequest;
import com.example.Music_streaming.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spotify")
@Tag(name = "Spotify Utility", description = "Spotify 토큰 저장 및 검색 유틸 API")
public class SpotifyController {

    private final SpotifyService spotifyService;
    private final UserService userService;

    @GetMapping("/search")
    @Operation(summary = "Spotify 트랙 검색 (Client Credentials)", description = "검색어로 Spotify Tracks API를 호출합니다. 개발/테스트용이며 사용자 토큰이 필요 없습니다.")
    public Map<String, Object> search(@RequestParam String query) {
        return spotifyService.searchTracks(query);
    }

    @PostMapping("/tokens")
    @Operation(summary = "Spotify 사용자 토큰 저장", description = "Authorization Code Flow 등으로 발급받은 Access/Refresh Token과 expiresIn을 Body로 전달하면 현재 로그인 사용자의 계정에 저장합니다.")
    public ResponseEntity<?> saveSpotifyTokens(@RequestBody SpotifyTokenRequest request) {
        userService.updateSpotifyTokens(request);
        return ResponseEntity.ok(Map.of("message", "Spotify 토큰이 저장되었습니다."));
    }
}
