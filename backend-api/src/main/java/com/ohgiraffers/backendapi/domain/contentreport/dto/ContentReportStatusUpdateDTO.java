package com.ohgiraffers.backendapi.domain.contentreport.dto;

import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "신고 처리 상태 변경 요청 DTO")
public class ContentReportStatusUpdateDTO {

    @Schema(description = "처리 의도 (ACCEPT, REJECT)", example = "ACCEPT")
    private String intent;

    @Schema(description = "관리자 메모 (선택)", example = "욕설 확인되어 삭제 처리함")
    private String adminNote;

    @Schema(description = "콘텐츠 노출 상태 변경 (선택) - BLINDED, SUSPENDED 등", example = "BLINDED")
    private VisibilityStatus visibilityStatus;
}
