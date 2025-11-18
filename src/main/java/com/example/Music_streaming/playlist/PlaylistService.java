package com.example.Music_streaming.playlist;

import com.example.Music_streaming.playlist.dto.AddTrackRequest;
import com.example.Music_streaming.playlist.dto.CreatePlaylistRequest;
import com.example.Music_streaming.playlist.dto.PlaylistResponse;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final UserRepository userRepository;

    // 현재 로그인한 유저 조회
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();  // JwtTokenProvider에서 subject로 email 넣어놨으니 이 값이 email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    // 플레이리스트 생성
    public PlaylistResponse createPlaylist(CreatePlaylistRequest request) {
        User currentUser = getCurrentUser();

        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isPublic(request.isPublic())
                .owner(currentUser)
                .build();

        Playlist saved = playlistRepository.save(playlist);

        return toResponse(saved);
    }

    // 내 플레이리스트 목록
    @Transactional(readOnly = true)
    public List<PlaylistResponse> getMyPlaylists() {
        User currentUser = getCurrentUser();
        return playlistRepository.findByOwner(currentUser)
                .stream()
                .map(this::toResponse)
                .collect(toList());
    }

    // 단일 플레이리스트 조회 (공개 or 내 것만)
    @Transactional(readOnly = true)
    public PlaylistResponse getPlaylist(Long id) {
        User currentUser = getCurrentUser();

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("플레이리스트가 존재하지 않습니다."));

        if (!playlist.isPublic() && !playlist.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        return toResponse(playlist);
    }

    // 트랙 추가
    public PlaylistResponse addTrack(Long playlistId, AddTrackRequest request) {
        User currentUser = getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("플레이리스트가 존재하지 않습니다."));

        if (!playlist.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("본인 플레이리스트에만 곡을 추가할 수 있습니다.");
        }

        int order = request.getOrder() != null
                ? request.getOrder()
                : (playlist.getTracks().size() + 1);

        PlaylistTrack track = PlaylistTrack.builder()
                .playlist(playlist)
                .title(request.getTitle())
                .artist(request.getArtist())
                .album(request.getAlbum())
                .thumbnailUrl(request.getThumbnailUrl())
                .durationMs(request.getDurationMs())
                .spotifyTrackId(request.getSpotifyTrackId())
                .trackOrder(order)
                .build();

        playlist.getTracks().add(track);
        playlistTrackRepository.save(track);

        return toResponse(playlist);
    }

    // 트랙 삭제
    public void removeTrack(Long playlistId, Long trackId) {
        User currentUser = getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("플레이리스트가 존재하지 않습니다."));

        if (!playlist.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("본인 플레이리스트만 수정할 수 있습니다.");
        }

        PlaylistTrack track = playlistTrackRepository.findById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("트랙이 존재하지 않습니다."));

        if (!track.getPlaylist().getId().equals(playlistId)) {
            throw new IllegalArgumentException("플레이리스트에 속한 트랙이 아닙니다.");
        }

        playlist.getTracks().remove(track);
        playlistTrackRepository.delete(track);
    }

    // Entity -> DTO 변환
    private PlaylistResponse toResponse(Playlist playlist) {
        return PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .isPublic(playlist.isPublic())
                .tracks(
                        playlist.getTracks().stream()
                                .map(t -> PlaylistResponse.TrackResponse.builder()
                                        .id(t.getId())
                                        .title(t.getTitle())
                                        .artist(t.getArtist())
                                        .album(t.getAlbum())
                                        .thumbnailUrl(t.getThumbnailUrl())
                                        .durationMs(t.getDurationMs())
                                        .spotifyTrackId(t.getSpotifyTrackId())
                                        .order(t.getTrackOrder())
                                        .build())
                                .collect(toList())
                )
                .build();
    }
}
