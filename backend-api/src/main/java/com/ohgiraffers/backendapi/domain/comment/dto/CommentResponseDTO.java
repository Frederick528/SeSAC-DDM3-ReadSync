package com.ohgiraffers.backendapi.domain.comment.dto;

import com.ohgiraffers.backendapi.global.common.enums.VisibilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "댓글 조회 응답 DTO")
public class CommentResponseDTO {

    private Long commentId;

    @Schema(description = "작성자 닉네임")
    private String nickname;

    private String content;

    @Schema(description = "부모 댓글 ID (대댓글인 경우", nullable = true)
    private Long parentCommentId;

    private boolean isSpoiler;

    private boolean isChanged;

    private VisibilityStatus status;

    private LocalDateTime createdAt;

}
