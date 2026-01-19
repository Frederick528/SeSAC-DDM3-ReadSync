package com.ohgiraffers.backendapi.domain.notice.entity;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int views;

    private Long adminId;   // 작성 관리자 ID (단순화)

    private LocalDateTime createdAt;

    private boolean deleted = false;

    /** ✅ 공지 생성용 생성자 (Service에서 사용하는 것) */
    public Notice(String title, String content, Long adminId) {
        this.title = title;
        this.content = content;
        this.adminId = adminId;
        this.views = 0;
        this.createdAt = LocalDateTime.now();
    }

    /** 조회수 증가 */
    public void increaseViews() {
        this.views++;
    }

    /** 수정 */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /** 삭제 (Soft Delete) */
    public void delete() {
        this.deleted = true;
    }
}
