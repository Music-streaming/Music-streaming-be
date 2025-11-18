package com.example.Music_streaming.comment.domain;

import com.example.Music_streaming.track.domain.Track;
import com.example.Music_streaming.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 댓글 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private Track track; // 어떤 음원에 달린 댓글인지

    @Enumerated(EnumType.STRING)
    private RatingType rating; // 만족, 불만족

    private int likeCount = 0;
    private int dislikeCount = 0;

    @Builder
    public Comment(Track track, User user, String content, RatingType ratingType) {
        this.track = track;
        this.user = user;
        this.content = content;
        this.rating = ratingType;
    }

    public void like() {
        this.likeCount += 1;
    }

    public void dislike() {
        this.dislikeCount += 1;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();


    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


}
