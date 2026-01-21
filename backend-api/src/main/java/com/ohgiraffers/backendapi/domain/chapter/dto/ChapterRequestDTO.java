package com.ohgiraffers.backendapi.domain.chapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "챕터 등록/수정 요청 DTO")
public class ChapterRequestDTO {

    @Schema(description = "책 ID", example = "1")
    private Long bookId;

    @Schema(description = "챕터명 (미입력 시 파일 내 정보 또는 자동 생성)")
    private String chapterName;

    @Schema(description = "챕터 순서 (미입력 시 파일 내 'chapter' 필드 사용)")
    private Integer sequence;

    @Schema(description = "챕터 JSON 파일")
    private MultipartFile file;
}
