package com.findDream.service;

import com.findDream.domain.Track;
import com.findDream.domain.User;
import com.findDream.repository.TrackRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TrackService {

    private final TrackRepository trackRepository;

    public TrackService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

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
    public List<Track> getTracksByUser(User user) {
        return trackRepository.findByUploader_UserId(user.getUserId());
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
