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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review (리뷰)", description = "도서별 리뷰 작성, 수정, 삭제, 조회 API")
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "특정 도서에 대한 새로운 리뷰 작성")
    @PostMapping
    public ResponseEntity<Long> createReview(
            @CurrentUserId Long userId,
            @RequestBody ReviewRequestDTO request
            ) {
        Long reviewId = reviewService.createReview(userId, request);
        return ResponseEntity.ok(reviewId);
    }

    @Operation(summary = "리뷰 단건 조회", description = "리뷰  ID로 리뷰 상세 정보 조회")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Long reviewId) {
        ReviewResponseDTO response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "도서별 리뷰 목록 조회(DELETED 제외)", description = "특정 도서에 작성된 리뷰 목록 조회")
    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewsByBook(
            @RequestParam Long bookId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ReviewResponseDTO> responses = reviewService.getReviewByBook(bookId, pageable);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "리뷰 수정", description = "작성자가 자신의 리뷰 내용, 별점, 스포일러 여부 수정")
    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(
            @PathVariable Long reviewId,
            @CurrentUserId Long userId,
            @RequestBody ReviewRequestDTO request
    ) {
        reviewService.updateReview(reviewId, userId,request);
        return ResponseEntity.ok("리뷰가 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "리뷰 삭제", description = "작성자가 자신의 리뷰 삭제(soft delete)")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId,
            @CurrentUserId Long userId
    ) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }
}
