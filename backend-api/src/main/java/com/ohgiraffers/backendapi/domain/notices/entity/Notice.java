package com.ohgiraffers.backendapi.domain.notices.entity;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private int views = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* ===== 비즈니스 로직 ===== */

    public void increaseViews() {
        this.views++;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
