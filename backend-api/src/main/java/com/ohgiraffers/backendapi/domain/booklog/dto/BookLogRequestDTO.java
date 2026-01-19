package com.ohgiraffers.backendapi.domain.booklog.dto;

import com.ohgiraffers.backendapi.domain.booklog.entity.BookLog;
import com.ohgiraffers.backendapi.domain.library.entity.Library;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLogRequestDTO {
    private Long libraryId;
    private Integer readTime;
    private Integer readParagraph;

    public BookLog toEntity(Library library, java.time.LocalDate date) {
        return BookLog.builder()
                .library(library)
                .readDate(date)
                .readTime(this.readTime)
                .readParagraph(this.readParagraph)
                .build();
    }
}
