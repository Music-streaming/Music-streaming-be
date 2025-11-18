package com.findDream.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;     // 곡 제목
    private String artist;    // 아티스트명
    private String url;       // 파일 경로 or 스트리밍 URL

    private int playCount = 0; // 재생 수
    private int likeCount = 0; // 좋아요 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User uploader;     // 업로드한 유저

    public Track(String title, String artist, String url, User uploader) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.uploader = uploader;
    }
}
