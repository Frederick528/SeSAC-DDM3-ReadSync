package com.ohgiraffers.backendapi.domain.like.controller;

import com.ohgiraffers.backendapi.domain.like.dto.LikeRequestDTO;
import com.ohgiraffers.backendapi.domain.like.dto.LikeResponseDTO;
import com.ohgiraffers.backendapi.domain.like.service.LikeService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/likes")
@RequiredArgsConstructor
@Tag(name = "Like (좋아요/싫어요)", description = "댓글 및 리뷰에 대한 좋아요/싫어요 기능 API")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "[사용자/관리자] 댓글 좋아요/싫어요 토글")
    @PostMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<LikeResponseDTO> toggleCommentLike(
            @Parameter(description = "대상 댓글 ID") @PathVariable Long commentId,
            @RequestBody LikeRequestDTO likeRequestDTO,
            @CurrentUserId Long userId) {
        LikeResponseDTO response = likeService.toggleCommentLike(userId, commentId, likeRequestDTO.getLikeType());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 리뷰 좋아요/싫어요 토글")
    @PostMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<LikeResponseDTO> toggleReviewLike(
            @Parameter(description = "대상 리뷰 ID") @PathVariable Long reviewId,
            @RequestBody LikeRequestDTO likeRequestDTO,
            @CurrentUserId Long userId) {
        LikeResponseDTO response = likeService.toggleReviewLike(userId, reviewId, likeRequestDTO.getLikeType());
        return ResponseEntity.ok(response);
    }
}
