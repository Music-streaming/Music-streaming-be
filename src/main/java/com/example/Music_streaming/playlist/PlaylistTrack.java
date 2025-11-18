package com.example.Music_streaming.playlist;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "playlist_tracks")
public class PlaylistTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 플레이리스트에 속한 트랙인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    // 곡 정보 (지금은 간단하게, 나중에 스포티파이랑 연동할 때 확장)
    @Column(nullable = false)
    private String title;

    private String artist;
    private String album;
    private String thumbnailUrl;
    private Integer durationMs;

    // 나중에 Spotify 연동할 때 쓸 track id
    private String spotifyTrackId;

    // 플레이리스트 내 순서
    private Integer trackOrder;
}
