package com.ohgiraffers.backendapi.domain.comment.entity;

import com.ohgiraffers.backendapi.domain.book.entity.Review;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import com.ohgiraffers.backendapi.domain.comment.enums.CommentReportReasonType;
import com.ohgiraffers.backendapi.domain.comment.enums.CommentReportTargetType;
import com.ohgiraffers.backendapi.global.common.enums.ReportProcessStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_reports")
public class CommentReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long report_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment commentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review reviewId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private CommentReportTargetType commentReportTargetType;
    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false, length = 20)
    private CommentReportReasonType commentReportReasonType;
    @Column(name = "reason_detail")
    private String reasonDetail;
    @Enumerated(EnumType.STRING)
    @Column(name = "process_status", nullable = false, length = 20)
    private ReportProcessStatus reportProcessStatus = ReportProcessStatus.PENDING;

    @Builder(builderMethodName = "CommentReportBuilder")
    public CommentReport(Comment commentId, User userId, CommentReportTargetType commentReportTargetType, CommentReportReasonType commentReportReasonType, String reasonDetail) {
        this.commentId = commentId;
        this.userId = userId;
        this.commentReportTargetType = commentReportTargetType;
        this.commentReportReasonType = commentReportReasonType;
        this.reasonDetail = reasonDetail;
        this.reportProcessStatus = ReportProcessStatus.PENDING;
    }

    @Builder(builderMethodName = "ReviewReportBuilder")
    public CommentReport(Review reviewId, User userId, CommentReportTargetType commentReportTargetType, CommentReportReasonType commentReportReasonType, String reasonDetail) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.commentReportTargetType = commentReportTargetType;
        this.commentReportReasonType = commentReportReasonType;
        this.reasonDetail = reasonDetail;
        this.reportProcessStatus = ReportProcessStatus.PENDING;
    }
}
