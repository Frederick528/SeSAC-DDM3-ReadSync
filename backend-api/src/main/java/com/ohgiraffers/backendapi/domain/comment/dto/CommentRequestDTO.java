package com.ohgiraffers.backendapi.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 작성/수정 요청 DTO")
public class CommentRequestDTO {

    @Schema(description = "댓글 내용", example = "정말 재미있네요!")
    @NotBlank(message = "댓글 내용은 필수")
    private String content;

    @Schema(description = "부모 댓글 ID (대댓글일 경우에만 값 존재, 일반 댓글은 null")
    private Long parentCommentId;   // null이면 일반 댓글로 간주

    @Schema(description = "스포일어 포함 여부", example = "false")
    private boolean isSpoiler;
}
