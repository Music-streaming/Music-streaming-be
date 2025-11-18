package com.example.Music_streaming.track.service;

import com.example.Music_streaming.track.domain.Track;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.track.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;

    // 음원 등록
    public Track uploadTrack(String title, String artist, String url, User uploader) {
        Track track = new Track(title, artist, url, uploader);
        return trackRepository.save(track);
    }

    // 전체 트랙 목록
    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    // 특정 유저의 트랙 목록
    public List<Track> getTracksByUserEmail(String email) {
        return trackRepository.findByUploader_Email(email);
    }

    // 트랙 삭제
    public void deleteTrack(Long id, User uploader) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("트랙을 찾을 수 없습니다."));
        if (!track.getUploader().getId().equals(uploader.getId())) {
            throw new IllegalStateException("본인이 업로드한 트랙만 삭제할 수 있습니다.");
        }
        trackRepository.delete(track);
    }
}
