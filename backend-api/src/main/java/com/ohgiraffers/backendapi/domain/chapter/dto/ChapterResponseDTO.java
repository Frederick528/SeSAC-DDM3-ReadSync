package com.ohgiraffers.backendapi.domain.chapter.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChapterResponseDTO {
    private Long chapterId;
    private Long bookId;
    private String chapterName;
    private Integer sequence;
    private String bookContentPath; // 디버깅용 파일 저장 경로(필수 아님. 필요 없다면 제외 가능)
    private Object bookContent; // 뷰어에서 보여줄 책 내용(JSON 객체)
    private Integer paragraphs; // 문단 개수
}
