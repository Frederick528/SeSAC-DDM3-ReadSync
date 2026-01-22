package com.ohgiraffers.backendapi.domain.chapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "챕터 응답 DTO")
public class ChapterResponseDTO {

    @Schema(description = "챕터 ID", example = "1")
    private Long chapterId;

    @Schema(description = "책 ID", example = "1")
    private Long bookId;

    @Schema(description = "챕터명", example = "만세전 Chapter 3")
    private String chapterName;

    @Schema(description = "챕터 순서", example = "3")
    private Integer sequence;

    @Schema(description = "paragraph 개수 (content 배열 내 id 개수)", example = "139")
    private Integer paragraphs;

    @Schema(description = "책 내용 URL (AWS S3)", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/chapters/xxx.json")
    private String contentUrl;

    @Schema(description = "임베딩 여부", example = "false")
    private Boolean isEmbedded;
}
