package com.example.Music_streaming.playlist;

import com.example.Music_streaming.playlist.dto.AddTrackRequest;
import com.example.Music_streaming.playlist.dto.CreatePlaylistRequest;
import com.example.Music_streaming.playlist.dto.PlaylistResponse;
import com.example.Music_streaming.playlist.dto.ReorderTracksRequest;
import com.example.Music_streaming.playlist.dto.UpdatePlaylistRequest;
import com.example.Music_streaming.spotify.SpotifyService;
import com.example.Music_streaming.spotify.TrackMetadata;
import com.example.Music_streaming.track.Track;
import com.example.Music_streaming.track.TrackRepository;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final UserService userService;
    private final SpotifyService spotifyService;
    private final TrackRepository trackRepository;

    /**
     * 플레이리스트 생성
     */
    public PlaylistResponse createPlaylist(CreatePlaylistRequest request) {

        User owner = userService.getCurrentUser();

        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isPublic(request.isPublic())
                .owner(owner)
                .build();

        playlistRepository.save(playlist);
        return PlaylistResponse.from(playlist);
    }

    /**
     * 내 플레이리스트 목록
     */
    public List<PlaylistResponse> getMyPlaylists() {

        User owner = userService.getCurrentUser();
        List<Playlist> playlists = playlistRepository.findByOwner(owner);

        return playlists.stream()
                .map(PlaylistResponse::from)
                .toList();
    }

    /**
     * 플레이리스트 상세조회
     */
    public PlaylistResponse getPlaylist(Long id) {

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));

        validateOwner(playlist);

        return PlaylistResponse.from(playlist);
    }

    /**
     * 트랙 추가 (Spotify trackId만 받음)
     */
    public PlaylistResponse addTrack(Long playlistId, AddTrackRequest request) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));

        validateOwner(playlist);

        TrackMetadata metadata = spotifyService.getTrackMetadata(request.getTrackId());
        Track trackEntity = trackRepository.findBySpotifyTrackId(request.getTrackId())
                .orElseGet(() -> trackRepository.save(Track.builder()
                        .spotifyTrackId(request.getTrackId())
                        .title(metadata.title())
                        .artist(metadata.artist())
                        .album(metadata.album())
                        .thumbnailUrl(metadata.thumbnailUrl())
                        .durationMs(metadata.durationMs())
                        .build()));

        PlaylistTrack track = PlaylistTrack.builder()
                .playlist(playlist)
                .spotifyTrackId(request.getTrackId())
                .title(trackEntity.getTitle())
                .artist(trackEntity.getArtist())
                .album(trackEntity.getAlbum())
                .thumbnailUrl(trackEntity.getThumbnailUrl())
                .durationMs(trackEntity.getDurationMs())
                .trackOrder(playlistTrackRepository.countByPlaylist(playlist) + 1)
                .build();
        playlistTrackRepository.save(track);
        playlist.getTracks().add(track);

        return PlaylistResponse.from(playlist);
    }

    /**
     * 트랙 삭제
     */
    public void removeTrack(Long playlistId, Long playlistTrackId) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));

        validateOwner(playlist);

        PlaylistTrack track = playlistTrackRepository.findById(playlistTrackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found"));

        if (!track.getPlaylist().equals(playlist)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "트랙이 해당 플레이리스트에 속하지 않습니다.");
        }

        playlistTrackRepository.delete(track);
        playlist.getTracks().removeIf(t -> t.getId().equals(track.getId()));
        reorderTracks(playlist);
    }

    public PlaylistResponse updatePlaylist(Long playlistId, UpdatePlaylistRequest request) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));
        validateOwner(playlist);

        if (request.getName() != null) {
            playlist.setName(request.getName());
        }
        if (request.getDescription() != null) {
            playlist.setDescription(request.getDescription());
        }
        if (request.getIsPublic() != null) {
            playlist.setPublic(request.getIsPublic());
        }
        playlistRepository.save(playlist);
        return PlaylistResponse.from(playlist);
    }

    public PlaylistResponse reorderTracks(Long playlistId, ReorderTracksRequest request) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));
        validateOwner(playlist);

        if (request.getOrderedTrackIds() == null || request.getOrderedTrackIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정렬할 트랙 목록이 필요합니다.");
        }

        List<PlaylistTrack> tracks = playlist.getTracks();
        if (tracks.size() != request.getOrderedTrackIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "모든 트랙 ID를 포함해야 합니다.");
        }

        Map<Long, PlaylistTrack> trackMap = tracks.stream()
                .collect(Collectors.toMap(PlaylistTrack::getId, t -> t));

        int order = 1;
        for (Long trackId : request.getOrderedTrackIds()) {
            PlaylistTrack playlistTrack = trackMap.get(trackId);
            if (playlistTrack == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 트랙 ID: " + trackId);
            }
            playlistTrack.setTrackOrder(order++);
        }
        playlistTrackRepository.saveAll(tracks);
        return PlaylistResponse.from(playlist);
    }

    private void validateOwner(Playlist playlist) {
        User current = userService.getCurrentUser();
        if (!playlist.getOwner().getId().equals(current.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 플레이리스트에 대한 권한이 없습니다.");
        }
    }

    private void reorderTracks(Playlist playlist) {
        List<PlaylistTrack> remaining = playlist.getTracks();
        remaining.sort((a, b) -> Integer.compare(a.getTrackOrder(), b.getTrackOrder()));
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setTrackOrder(i + 1);
        }
    }
}
