package com.ohgiraffers.backendapi.domain.contentreport.dto;

import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportReasonType;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "콘텐츠(리뷰/댓글) 신고 요청 DTO")
public class ContentReportRequestDTO {

    @Schema(description = "신고 대상 유형 (CHAPTERS_COMMENT, REVIEW)", example = "REVIEW")
    private ContentReportTargetType targetType;

    @Schema(description = "신고 대상 ID (댓글ID 또는 리뷰ID)", example = "1")
    private Long targetId;

    @Schema(description = "신고 사유 유형 (BAD_LANGUAGE, SPOILER, ADVERTISEMENT, OTHER)", example = "ABUSE")
    private ContentReportReasonType reasonType;

    @Schema(description = "상세 사유 (기타 선택 시 작성)", example = "지나친 욕설이 포함되어 있습니다.")
    private String reasonDetail;
}
