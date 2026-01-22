package com.ohgiraffers.backendapi.domain.comment.entity;

import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportReasonType;
import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

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
    private boolean isChanged = false;

    @Column(name = "is_spoiler", nullable = false)
    private boolean isSpoiler = false;

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
    public Comment(User user, Chapter chapter, String content, Comment parentComment, boolean isSpoiler) {
        this.user = user;
        this.chapter = chapter;
        this.content = content;
        this.parentComment = parentComment;
        this.isSpoiler = isSpoiler;
        this.visibilityStatus = VisibilityStatus.ACTIVE;
        this.isChanged = false;
        this.spoilerReportCount = 0; // Initialize in constructor
        this.violationReportCount = 0; // Initialize in constructor
    }

    // 댓글 수정 로직
    public void updateContent(String newContent, Boolean isSpoiler) {
        // 내용이 실제로 바뀌었을 때만 변경 처리
        if (newContent != null && !this.content.equals(newContent)) {
            this.content = newContent;
            this.isChanged = true;
        }
        if (isSpoiler != null) {
            this.isSpoiler = isSpoiler;
        }
    }

    // 댓글 삭제(soft delete) 로직
    public void delete() {
        this.visibilityStatus = VisibilityStatus.DELETED;
    }

    public void changeVisibility(VisibilityStatus status) {
        this.visibilityStatus = status;
    }

    // 신고 누적 처리 로직
    public void incrementReportCount(ContentReportReasonType reasonType) {
        if (reasonType == ContentReportReasonType.SPOILER) {
            this.spoilerReportCount++;
            if (this.spoilerReportCount >= 5) {
                this.visibilityStatus = VisibilityStatus.BLINDED;
            }
        } else if (reasonType == ContentReportReasonType.ABUSE || reasonType == ContentReportReasonType.ADVERTISEMENT) {
            this.violationReportCount++;
            if (this.violationReportCount >= 5) {
                this.visibilityStatus = VisibilityStatus.SUSPENDED;
            }
        }
    }
}