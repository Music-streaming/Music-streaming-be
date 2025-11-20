package com.example.Music_streaming.track;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tracks")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기본 메타데이터
    @Column(nullable = false)
    private String title;

    private String artist;
    private String album;
    private String thumbnailUrl;
    private Integer durationMs;

    // 외부 서비스 ID
    private String spotifyTrackId;
    private String youtubeVideoId;

    // 좋아요 카운트 (성능용 캐시)
    @Builder.Default
    @Column(nullable = false)
    private long likeCount = 0L;
}
