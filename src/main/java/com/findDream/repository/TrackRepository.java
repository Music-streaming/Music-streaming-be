package com.findDream.repository;

import com.findDream.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByUploader_UserId(String userId); // 특정 사용자의 업로드 목록 조회
}
