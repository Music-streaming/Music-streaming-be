package com.example.Music_streaming.like;

import com.example.Music_streaming.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "spotify_track_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"spotify_track_id", "user_id"})
)
public class SpotifyTrackLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spotify_track_id", nullable = false)
    private String spotifyTrackId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
