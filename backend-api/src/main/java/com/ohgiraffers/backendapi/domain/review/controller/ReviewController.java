package com.ohgiraffers.backendapi.domain.review.controller;

import com.ohgiraffers.backendapi.domain.review.dto.ReviewRequestDTO;
import com.ohgiraffers.backendapi.domain.review.dto.ReviewResponseDTO;
import com.ohgiraffers.backendapi.domain.review.service.ReviewService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review (리뷰)", description = "도서별 리뷰 작성, 수정, 삭제, 조회 API")
@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "[사용자/관리자] 리뷰 작성", description = "특정 도서에 대한 새로운 리뷰 작성(USER)")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Long> createReview(
            @CurrentUserId Long userId,
            @RequestBody ReviewRequestDTO request) {
        Long reviewId = reviewService.createReview(userId, request);
        return ResponseEntity.ok(reviewId);
    }

    @Operation(summary = "[누구나] 리뷰 단건 조회", description = "리뷰  ID로 리뷰 상세 정보 조회")
    @GetMapping("/{reviewId}")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Long reviewId) {
        ReviewResponseDTO response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[누구나] 도서별 리뷰 목록 조회(DELETED 제외)", description = "특정 도서에 작성된 리뷰 목록 조회")
    @GetMapping
    // @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewsByBook(
            @RequestParam Long bookId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReviewResponseDTO> responses = reviewService.getReviewByBook(bookId, pageable);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "[사용자/관리자] 내 리뷰 목록 조회", description = "본인이 작성한 리뷰 목록 조회(페이징)")
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<ReviewResponseDTO>> getMyReviews(
            @CurrentUserId Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReviewResponseDTO> responses = reviewService.getMyReviews(userId, pageable);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "[사용자/관리자] 리뷰 수정", description = "작성자가 자신의 리뷰 내용, 별점, 스포일러 여부 수정")
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> updateReview(
            @PathVariable Long reviewId,
            @CurrentUserId Long userId,
            @RequestBody ReviewRequestDTO request) {
        reviewService.updateReview(reviewId, userId, request);
        return ResponseEntity.ok("리뷰가 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "[사용자/관리자] 리뷰 삭제", description = "작성자가 자신의 리뷰 삭제(soft delete)")
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId,
            @CurrentUserId Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }

    // --- Admin Endpoints ---

    @Operation(summary = "[관리자] 리뷰 전체 조회", description = "모든 리뷰를 조회합니다.{ \"page\": 0, \"size\": 10, \"sort\": \"ASC\" }")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReviewResponseDTO>> getAllReviewsAdmin(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReviewResponseDTO> responses = reviewService.getAllReviewsAdmin(pageable);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "[관리자] 리뷰 강제 삭제", description = "관리자가 리뷰를 강제로 삭제합니다.")
    @DeleteMapping("/admin/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReviewAdmin(
            @Parameter(description = "삭제할 리뷰 ID") @PathVariable Long reviewId) {
        reviewService.deleteReviewAdmin(reviewId);
        return ResponseEntity.noContent().build();
    }
}
