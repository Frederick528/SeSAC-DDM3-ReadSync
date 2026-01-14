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
    private Long comment_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapterId;
    @Column(name = "comment_content", nullable = false)
    private String commentContent;
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

    // 1. 일반 댓글용 빌더
    @Builder(builderMethodName = "createComment")
    public Comment(User user_id, Chapter chapterId, String commentContent, Boolean isSpoiler) {
        this.user_id = user_id;
        this.chapterId = chapterId;
        this.commentContent = commentContent;
        this.isSpoiler = isSpoiler;
        this.visibilityStatus = VisibilityStatus.ACTIVE;
    }

    // 2. 대댓글(답글)용 빌더
    @Builder(builderMethodName = "createReply")
    public Comment(User user_id, Comment parent_id, Chapter chapterId, String commentContent, Boolean isSpoiler) {
        this.user_id = user_id;
        this.parent_id = parent_id;
        this.chapterId = chapterId;
        this.commentContent = commentContent;
        this.isSpoiler = isSpoiler;
        this.visibilityStatus = VisibilityStatus.ACTIVE;  
    }
}
