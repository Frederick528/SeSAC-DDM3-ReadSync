package com.ohgiraffers.backendapi.domain.contentreport.service;

import com.ohgiraffers.backendapi.domain.comment.entity.Comment;
import com.ohgiraffers.backendapi.domain.comment.repository.CommentRepository;
import com.ohgiraffers.backendapi.domain.contentreport.dto.ContentReportDetailResponseDTO;
import com.ohgiraffers.backendapi.domain.contentreport.dto.ContentReportRequestDTO;
import com.ohgiraffers.backendapi.domain.contentreport.dto.ContentReportResponseDTO;
import com.ohgiraffers.backendapi.domain.contentreport.dto.ContentReportStatusUpdateDTO;
import com.ohgiraffers.backendapi.domain.contentreport.entity.ContentReport;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportProcessStatus;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportTargetType;
import com.ohgiraffers.backendapi.domain.contentreport.repository.ContentReportRepository;
import com.ohgiraffers.backendapi.domain.review.entity.Review;
import com.ohgiraffers.backendapi.domain.review.repository.ReviewRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentReportService {

    private final ContentReportRepository contentReportRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    public Long createContentReport(Long userId, ContentReportRequestDTO requestDTO) {
        // Validate Target Existence and Increment Report Count
        if (requestDTO.getTargetType() == ContentReportTargetType.REVIEW) {
            Review review = reviewRepository.findById(requestDTO.getTargetId())
                    .orElseThrow(
                            () -> new CustomException(ErrorCode.REVIEW_NOT_FOUND, "ID: " + requestDTO.getTargetId()));
            review.incrementReportCount(requestDTO.getReasonType());
        } else if (requestDTO.getTargetType() == ContentReportTargetType.CHAPTERS_COMMENT) {
            Comment comment = commentRepository.findById(requestDTO.getTargetId())
                    .orElseThrow(
                            () -> new CustomException(ErrorCode.COMMENT_NOT_FOUND, "ID: " + requestDTO.getTargetId()));
            comment.incrementReportCount(requestDTO.getReasonType());
        }

        ContentReport report = ContentReport.builder()
                .reporterId(userId)
                .targetType(requestDTO.getTargetType())
                .reasonType(requestDTO.getReasonType())
                .reasonDetail(requestDTO.getReasonDetail())
                .reviewId(
                        requestDTO.getTargetType() == ContentReportTargetType.REVIEW ? requestDTO.getTargetId() : null)
                .commentId(requestDTO.getTargetType() == ContentReportTargetType.CHAPTERS_COMMENT
                        ? requestDTO.getTargetId()
                        : null)
                .build();

        return contentReportRepository.save(report).getReportId();
    }

    @Transactional(readOnly = true)
    public Page<ContentReportResponseDTO> getContentReports(ContentReportProcessStatus status,
            ContentReportTargetType targetType, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ContentReport> reports = contentReportRepository.findByStatusAndTargetType(status, targetType, pageable);
        return reports.map(ContentReportResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public ContentReportDetailResponseDTO getContentReportDetail(Long reportId) {
        ContentReport report = contentReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND, "ID: " + reportId));

        String content = "";
        if (report.getTargetType() == ContentReportTargetType.REVIEW) {
            Review review = reviewRepository.findById(report.getReviewId()).orElse(null);
            content = (review != null) ? review.getReviewContent() : "삭제된 리뷰입니다.";
        } else if (report.getTargetType() == ContentReportTargetType.CHAPTERS_COMMENT) {
            Comment comment = commentRepository.findById(report.getCommentId()).orElse(null);
            content = (comment != null) ? comment.getContent() : "삭제된 댓글입니다.";
        }

        return ContentReportDetailResponseDTO.from(report, content);
    }

    public ContentReportResponseDTO updateContentReportStatus(Long reportId, ContentReportStatusUpdateDTO updateDTO) {
        ContentReport report = contentReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND, "ID: " + reportId));

        ContentReportProcessStatus newStatus;
        if ("ACCEPT".equalsIgnoreCase(updateDTO.getIntent())) {
            newStatus = ContentReportProcessStatus.ACCEPTED;
        } else if ("REJECT".equalsIgnoreCase(updateDTO.getIntent())) {
            newStatus = ContentReportProcessStatus.REJECTED;
        } else {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "처리 의도는 ACCEPT 또는 REJECT여야 합니다.");
        }

        report.updateStatus(newStatus);

        // Visibility Update Logic
        if (updateDTO.getVisibilityStatus() != null) {
            if (report.getTargetType() == ContentReportTargetType.REVIEW) {
                reviewRepository.findById(report.getReviewId())
                        .ifPresent(review -> review.changeVisibility(updateDTO.getVisibilityStatus()));
            } else if (report.getTargetType() == ContentReportTargetType.CHAPTERS_COMMENT) {
                commentRepository.findById(report.getCommentId())
                        .ifPresent(comment -> comment.changeVisibility(updateDTO.getVisibilityStatus()));
            }
        }

        return ContentReportResponseDTO.from(report);
    }
}
