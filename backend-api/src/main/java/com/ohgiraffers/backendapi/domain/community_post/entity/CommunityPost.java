package com.ohgiraffers.backendapi.domain.community_post.entity;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "community_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private int views = 0;

    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private int report = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* ===== 비즈니스 로직 ===== */

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void increaseViews() {
        this.views++;
    }
}
