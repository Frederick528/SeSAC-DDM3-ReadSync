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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comment (댓글)", description = "챕터별 댓글 작성, 수정, 삭제, 조회 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "[사용자/관리자] 댓글 작성", description = "특정 챕터에 댓글/대댓글 작성(USER)")
    @PostMapping("/{chapterId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentResponseDTO> createComment(
            @CurrentUserId Long userId,
            @Parameter(description = "챕터ID") @PathVariable Long chapterId,
            @RequestBody CommentRequestDTO requestDTO) {
        CommentResponseDTO response = commentService.createComment(userId, chapterId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 댓글 단건 조회", description = "댓글 ID로 댓글 상세 정보 조회")
    @GetMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentResponseDTO> getComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {
        CommentResponseDTO response = commentService.getComment(commentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[누구나] 챕터별 댓글 목록 조회", description = "특정 챕터의 모든 댓글 조회")
    @GetMapping("/chapter/{chapterId}")
    // @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CommentResponseDTO>> getComments(
            @Parameter(description = "챕터 ID") @PathVariable Long chapterId) {
        List<CommentResponseDTO> response = commentService.getCommentsByChapter(chapterId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 내 댓글 목록 조회", description = "본인이 작성한 댓글 목록 조회")
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CommentResponseDTO>> getMyComments(
            @CurrentUserId Long userId) {
        List<CommentResponseDTO> response = commentService.getMyComments(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 댓글 수정", description = "작성자가 자신의 댓글 수정(작성자만 가능)")
    @PatchMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @CurrentUserId Long userId,
            @Parameter(description = "수정할 댓글 ID") @PathVariable Long commentId,
            @RequestBody CommentRequestDTO requestDTO) {
        CommentResponseDTO response = commentService.updateComment(userId, commentId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[사용자/관리자] 댓글 삭제", description = "작성자가 자신의 댓글 삭제(soft delete)")
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteComment(
            @CurrentUserId Long userId,
            @Parameter(description = "삭제할 댓글 ID") @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }

    // --- Admin Endpoints ---

    @Operation(summary = "[관리자] 댓글 전체 조회", description = "모든 댓글을 조회합니다.(관리자 전용)")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentResponseDTO>> getAllCommentsAdmin() {
        List<CommentResponseDTO> response = commentService.getAllCommentsAdmin();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 댓글 강제 삭제", description = "관리자가 댓글을 강제로 삭제합니다.(관리자 전용)")
    @DeleteMapping("/admin/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCommentAdmin(
            @Parameter(description = "삭제할 댓글 ID") @PathVariable Long commentId) {
        commentService.deleteCommentAdmin(commentId);
        return ResponseEntity.noContent().build();
    }

}
