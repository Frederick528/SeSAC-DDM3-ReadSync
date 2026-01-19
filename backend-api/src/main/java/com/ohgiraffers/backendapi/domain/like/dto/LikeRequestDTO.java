package com.ohgiraffers.backendapi.domain.like.dto;

import com.ohgiraffers.backendapi.domain.like.enums.LikeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "좋아요/싫어요 요청 DTO")
public class LikeRequestDTO {

    @Schema(description = "반응 유형 'LIKE'or'DISLIKE'", example = "LIKE")
    @NotNull(message = "반응 유형은 필수입니다.")
    private LikeType likeType;
}