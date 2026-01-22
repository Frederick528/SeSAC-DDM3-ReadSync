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
@Schema(description = "콘텐츠 신고 응답 DTO")
public class ContentReportResponseDTO {

    @Schema(description = "신고 ID")
    private Long reportId;

    @Schema(description = "신고자 ID")
    private Long reporterId;

    @Schema(description = "대상 유형")
    private ContentReportTargetType targetType;

    @Schema(description = "대상 ID")
    private Long targetId;

    @Schema(description = "신고 사유 유형")
    private ContentReportReasonType reasonType;

    @Schema(description = "처리 상태")
    private ContentReportProcessStatus processStatus;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    public static ContentReportResponseDTO from(ContentReport report) {
        Long targetId = (report.getTargetType() == ContentReportTargetType.REVIEW) ? report.getReviewId()
                : report.getCommentId();
        return ContentReportResponseDTO.builder()
                .reportId(report.getReportId())
                .reporterId(report.getReporterId())
                .targetType(report.getTargetType())
                .targetId(targetId)
                .reasonType(report.getReasonType())
                .processStatus(report.getProcessStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
