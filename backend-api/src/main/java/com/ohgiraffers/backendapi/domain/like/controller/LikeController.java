package com.ohgiraffers.backendapi.domain.like.controller;

import com.ohgiraffers.backendapi.domain.like.dto.LikeRequestDTO;
import com.ohgiraffers.backendapi.domain.like.dto.LikeResponseDTO;
import com.ohgiraffers.backendapi.domain.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Tag(name = "Like (좋아요/싫어요)", description = "댓글 및 리뷰에 대한 좋아요/싫어요 기능 API")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "댓글 좋아요/싫어요 토글")
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<LikeResponseDTO> toggleCommentLike(
            @Parameter(description = "대상 댓글 ID") @PathVariable Long commentId,
            @RequestBody LikeRequestDTO likeRequestDTO
            /* , @AuthenticationPrincipal Long userId */
            ) {
        Long tempUserId = 1L; // 임시 유저 ID(나중에 로그인 기능 구현되면 지울 것
        LikeResponseDTO response = likeService.toggleCommentLike(tempUserId, commentId, likeRequestDTO.getLikeType());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 좋아요/싫어요 토글")
    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<LikeResponseDTO> toggleReviewLike(
            @Parameter(description = "대상 리뷰 ID") @PathVariable Long reviewId,
            @RequestBody LikeRequestDTO likeRequestDTO
            /* , @AuthenticationPrincipal Long userId */
            ) {
        Long tempUserId = 1L; // 임시 유저 ID(나중에 로그인 기능 구현되면 지우고 밑의 파라미터도 변경할 것
        LikeResponseDTO response = likeService.toggleReviewLike(tempUserId, reviewId, likeRequestDTO.getLikeType());
        return ResponseEntity.ok(response);
    }
}
