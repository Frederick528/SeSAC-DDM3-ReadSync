package com.ohgiraffers.backendapi.domain.like.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "좋아요 처리 결과 및 집계 DTO")
public class LikeResponseDTO {
    private boolean isPressed;
    @Schema(description = "처리 메시지", example = "좋아요가 반영되었습니다.")
    private String message;
    private Long totalLikes;
    private Long totalDislikes;
}