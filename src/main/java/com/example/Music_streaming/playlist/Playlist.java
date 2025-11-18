package com.example.Music_streaming.playlist;

import com.example.Music_streaming.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 플레이리스트 이름
    @Column(nullable = false)
    private String name;

    // 설명(옵션)
    private String description;

    // 공개 여부 (true: 전체공개, false: 나만보기)
    @Column(nullable = false)
    private boolean isPublic;

    // 소유자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // 트랙 목록
    @OneToMany(mappedBy = "playlist",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("trackOrder ASC")
    private List<PlaylistTrack> tracks = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
