package com.ohgiraffers.backendapi.domain.comment.entity;

import com.ohgiraffers.backendapi.domain.book.entity.Chapter;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment extends BaseTimeEntity {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // 부모 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;
    @Column(name = "comment_content", nullable = false)
    private String content;
    @Column(name = "is_changed", nullable = false)
    private Boolean isChanged = false;
    @Column(name = "is_spoiler", nullable = false)
    private Boolean isSpoiler = false;
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_status", nullable = false, length = 20)
    private VisibilityStatus visibilityStatus;
    @Column(name = "spoiler_report_count", nullable = false)
    private Integer spoilerReportCount = 0;
    @Column(name = "violation_report_count", nullable = false)
    private Integer violationReportCount = 0;
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;
    @Column(name = "dislike_count", nullable = false)
    private Integer dislikeCount = 0;

    // Service에서 parentComment에 null을 넣으면 일반 댓글, 객체를 넣으면 대댓글
    @Builder
    public Comment(User user, Chapter chapter, String content, Comment parentComment, Boolean isSpoiler) {
        this.user = user;
        this.chapter = chapter;
        this.content = content;
        this.parentComment = parentComment;
        this.isSpoiler = isSpoiler;
        this.visibilityStatus = VisibilityStatus.ACTIVE;
        this.isChanged = false;
    }
    // 댓글 수정 로직
    public void updateContent(String newContent) {
        // 내용이 실제로 바뀌었을 때만 변경 처리
        if(newContent != null && !this.content.equals(newContent)) {
            this.content = newContent;
            this.isChanged = true;
        }
    }
}