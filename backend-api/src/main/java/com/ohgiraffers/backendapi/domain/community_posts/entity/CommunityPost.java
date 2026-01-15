package com.ohgiraffers.backendapi.domain.community_posts.entity;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "community_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int views = 0;

    @Column(nullable = false)
    private int report = 0;

    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* ===== 비즈니스 메서드 ===== */

    public void increaseViews() {
        this.views++;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
