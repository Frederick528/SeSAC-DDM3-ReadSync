package com.ohgiraffers.backendapi.domain.contentreport.dto;

import com.ohgiraffers.backendapi.domain.contentreport.entity.ContentReport;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportProcessStatus;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportReasonType;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "콘텐츠 신고 상세 응답 DTO")
public class ContentReportDetailResponseDTO {

    @Schema(description = "신고 ID")
    private Long reportId;

    @Schema(description = "신고자 ID")
    private Long reporterId;

    @Schema(description = "대상 유형")
    private ContentReportTargetType targetType;

    @Schema(description = "대상 ID")
    private Long targetId;

    @Schema(description = "대상 원문 내용")
    private String targetContent;

    @Schema(description = "신고 사유 유형")
    private ContentReportReasonType reasonType;

    @Schema(description = "상세 사유")
    private String reasonDetail;

    @Schema(description = "처리 상태")
    private ContentReportProcessStatus processStatus;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    public static ContentReportDetailResponseDTO from(ContentReport report, String content) {
        Long targetId = (report.getTargetType() == ContentReportTargetType.REVIEW) ? report.getReviewId()
                : report.getCommentId();
        return ContentReportDetailResponseDTO.builder()
                .reportId(report.getReportId())
                .reporterId(report.getReporterId())
                .targetType(report.getTargetType())
                .targetId(targetId)
                .targetContent(content)
                .reasonType(report.getReasonType())
                .reasonDetail(report.getReasonDetail())
                .processStatus(report.getProcessStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
