package com.ohgiraffers.backendapi.domain.comment.controller;

import com.ohgiraffers.backendapi.domain.comment.dto.CommentRequestDTO;
import com.ohgiraffers.backendapi.domain.comment.dto.CommentResponseDTO;
import com.ohgiraffers.backendapi.domain.comment.service.CommentService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comment (댓글", description = "챕터별 댓글 작성, 수정, 삭제, 조회 API")
public class CommentController {

    private final CommentService commentService;

    // TODO: 추후 @AuthenticationPrincipal로 userId 교체 필요

    @Operation(summary = "댓글 작성", description = "특정 챕터에 댓글/대댓글 작성")
    @PostMapping("/{chapterId}")
    public ResponseEntity<CommentResponseDTO> createComment(
            @CurrentUserId Long userId,
            @Parameter(description = "챕터ID") @PathVariable Long chapterId,
            @RequestBody CommentRequestDTO requestDTO
    ) {
        CommentResponseDTO response = commentService.createComment(userId, chapterId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "댓글 목록 조회", description = "특정 챕터의 모든 댓글 조회")
    @GetMapping("/{chapterId}")
    public ResponseEntity<List<CommentResponseDTO>> getComments(
            @Parameter(description = "챕터 ID") @PathVariable Long chapterId
    ) {
        List<CommentResponseDTO> response = commentService.getCommentsByChapter(chapterId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "댓글 수정", description = "작성자가 자신의 댓글 수정(작성자만 가능)")
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @CurrentUserId Long userId,
            @Parameter(description = "수정할 댓글 ID") @PathVariable Long commentId,
            @RequestBody CommentRequestDTO requestDTO
    ) {
        CommentResponseDTO response = commentService.updateComment(userId, commentId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "댓글 삭제", description = "작성가자 자신의 댓글 삭제(soft delete)")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @CurrentUserId Long userId,
            @Parameter(description = "삭제할 댓글 ID") @PathVariable Long commentId
    ) {
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }

}
