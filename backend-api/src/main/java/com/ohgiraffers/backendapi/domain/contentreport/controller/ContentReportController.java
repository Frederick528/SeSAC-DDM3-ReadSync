package com.ohgiraffers.backendapi.domain.contentreport.controller;

import com.ohgiraffers.backendapi.domain.contentreport.dto.ContentReportDetailResponseDTO;
import com.ohgiraffers.backendapi.domain.contentreport.dto.ContentReportRequestDTO;
import com.ohgiraffers.backendapi.domain.contentreport.dto.ContentReportResponseDTO;
import com.ohgiraffers.backendapi.domain.contentreport.dto.ContentReportStatusUpdateDTO;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportProcessStatus;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportTargetType;
import com.ohgiraffers.backendapi.domain.contentreport.service.ContentReportService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/contentreports") // Changed URL as requested
@RequiredArgsConstructor
@Tag(name = "Content Report (댓글/리뷰 신고)", description = "댓글 및 리뷰 신고 관리 API")
public class ContentReportController {

    private final ContentReportService contentReportService;

    @Operation(summary = "[사용자/관리자] 신고 생성", description = "댓글 또는 리뷰를 신고합니다.")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> createContentReport(
            @CurrentUserId Long userId,
            @RequestBody ContentReportRequestDTO requestDTO) {
        Long reportId = contentReportService.createContentReport(userId, requestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("reportId", reportId);
        response.put("processStatus", ContentReportProcessStatus.PENDING);
        response.put("createdAt", java.time.LocalDateTime.now()); // Approximate for response

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 신고 목록 조회", description = "관리자가 신고 목록을 조회합니다.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getContentReports(
            @Parameter(description = "처리 상태 (PENDING, ACCEPTED, REJECTED)") @RequestParam(required = false) ContentReportProcessStatus status,
            @Parameter(description = "대상 유형 (CHAPTERS_COMMENT, REVIEW)") @RequestParam(required = false) ContentReportTargetType targetType,
            @Parameter(description = "페이지 번호 (기본: 1)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기 (기본: 10)") @RequestParam(defaultValue = "10") int size) {

        Page<ContentReportResponseDTO> reportPage = contentReportService.getContentReports(status, targetType, page,
                size);

        Map<String, Object> response = new HashMap<>();
        response.put("reports", reportPage.getContent());
        response.put("totalCount", reportPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 신고 상세 조회", description = "관리자가 신고 상세 내용을 조회합니다.")
    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContentReportDetailResponseDTO> getContentReportDetail(
            @Parameter(description = "신고 ID") @PathVariable Long reportId) {
        ContentReportDetailResponseDTO response = contentReportService.getContentReportDetail(reportId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 신고 처리", description = "관리자가 신고를 승인하거나 반려합니다.")
    @PatchMapping("/{reportId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContentReportResponseDTO> updateContentReportStatus(
            @Parameter(description = "신고 ID") @PathVariable Long reportId,
            @RequestBody ContentReportStatusUpdateDTO updateDTO) {
        ContentReportResponseDTO response = contentReportService.updateContentReportStatus(reportId, updateDTO);
        return ResponseEntity.ok(response);
    }
}
