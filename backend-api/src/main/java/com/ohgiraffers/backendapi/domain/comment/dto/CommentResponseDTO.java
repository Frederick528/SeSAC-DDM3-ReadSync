package com.ohgiraffers.backendapi.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("isSpoiler")
    private boolean spoiler; // Jackson이 내부 문법에 따라서 isSpoiler를 spoiler로 자동 변환하기 때문에 spoiler로 애매하지 않게끔 만듦.

    private boolean isChanged;

    private VisibilityStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime changedAt;

}
