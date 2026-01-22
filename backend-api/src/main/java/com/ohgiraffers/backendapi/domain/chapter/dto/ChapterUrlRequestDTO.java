package com.ohgiraffers.backendapi.domain.chapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "URL 기반 챕터 등록/수정 요청 DTO")
public class ChapterUrlRequestDTO {

    @Schema(description = "책 ID", example = "1")
    private Long bookId;

    @Schema(description = "챕터명", example = "1장: 관계형 모델")
    private String chapterName;

    @Schema(description = "챕터 순서", example = "1")
    private Integer sequence;

    @Schema(description = "챕터 컨텐츠 URL", example = "https://docs.google.com/uc?export=download&id=1GKS0nTkwUaH07YRAY0U0Tt0S6p-j_rD7")
    private String contentUrl;

    @Schema(description = "문단 개수 (-1은 미설정)", example = "50")
    private Integer paragraphs;
}
