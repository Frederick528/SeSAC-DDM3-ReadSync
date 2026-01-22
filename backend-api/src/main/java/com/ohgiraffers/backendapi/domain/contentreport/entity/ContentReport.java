package com.ohgiraffers.backendapi.domain.contentreport.entity;

import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportProcessStatus;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportReasonType;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportTargetType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "content_reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ContentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "user_id", nullable = false)
    private Long reporterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ContentReportTargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false)
    private ContentReportReasonType reasonType;

    @Column(name = "reason_detail", columnDefinition = "TEXT")
    private String reasonDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_status", nullable = false)
    private ContentReportProcessStatus processStatus;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ContentReport(Long commentId, Long reviewId, Long reporterId, ContentReportTargetType targetType,
            ContentReportReasonType reasonType, String reasonDetail) {
        this.commentId = commentId;
        this.reviewId = reviewId;
        this.reporterId = reporterId;
        this.targetType = targetType;
        this.reasonType = reasonType;
        this.reasonDetail = reasonDetail;
        this.processStatus = ContentReportProcessStatus.PENDING;
    }

    public void updateStatus(ContentReportProcessStatus status) {
        this.processStatus = status;
    }
}
