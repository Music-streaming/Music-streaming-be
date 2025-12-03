package com.example.Music_streaming.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor // JPA는 기본 생성자가 필수입니다.
@Table(name = "users") // DB 테이블 이름을 명시적으로 'users'로 지정
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 ID를 자동으로 생성
    private Long id;

    @Column(nullable = false, unique = true, length = 50) // null 불가, 유니크, 길이 50
    private String email;

    @Column(nullable = false)
    private String password; // 암호화되어 저장될 예정

    @Column(nullable = false, length = 30)
    private String username;

    @Column(name = "spotify_access_token")
    private String spotifyAccessToken;

    @Column(name = "spotify_refresh_token")
    private String spotifyRefreshToken;

    @Column(name = "spotify_token_expires_at")
    private Instant spotifyTokenExpiresAt;

    // 빌더 패턴: 객체 생성을 깔끔하게 도와줍니다.
    @Builder
    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public void updateSpotifyTokens(String accessToken, String refreshToken, Instant expiresAt) {
        this.spotifyAccessToken = accessToken;
        if (refreshToken != null && !refreshToken.isBlank()) {
            this.spotifyRefreshToken = refreshToken;
        }
        this.spotifyTokenExpiresAt = expiresAt;
    }

    public boolean hasValidSpotifyAccessToken() {
        if (spotifyAccessToken == null || spotifyAccessToken.isBlank()) {
            return false;
        }
        if (spotifyTokenExpiresAt == null) {
            return true;
        }
        return spotifyTokenExpiresAt.isAfter(Instant.now().minusSeconds(30));
    }
}
