package com.ohgiraffers.backendapi.domain.bookmark.dto;

import com.ohgiraffers.backendapi.domain.bookmark.entity.Bookmark;
import com.ohgiraffers.backendapi.domain.chapter.entity.Chapter;
import com.ohgiraffers.backendapi.domain.library.entity.Library;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkRequestDTO {

    private Long libraryId;

    private Long chapterId;
    private Integer lastReadPos;
    @Schema(description = "읽은 문단 번호 리스트", example = "[1, 2, 3, 4]")
    private List<Integer> readParagraphIndices;


    // 맨 처음 생성
    public Bookmark toEntity(Library library, Chapter chapter, byte[] initialMask) {
        return Bookmark.builder()
                .library(library)
                .chapter(chapter)
                .lastReadPos(lastReadPos)
                .progress(BigDecimal.ZERO)
                .readMask(initialMask)
                .build();
    }
}