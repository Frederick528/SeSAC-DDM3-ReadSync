package com.ohgiraffers.backendapi.domain.booklog.dto;

import com.ohgiraffers.backendapi.domain.book.dto.BookResponseDTO;
import com.ohgiraffers.backendapi.domain.booklog.entity.BookLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLogResponseDTO {
    private Long bookLogId;
    private Long libraryId;
    private LocalDate readDate;
    private Integer readTime;
    private Integer readParagraph;

    public static BookLogResponseDTO from(BookLog bookLog) {
        return BookLogResponseDTO.builder()
                .bookLogId(bookLog.getBookLogId())
                .libraryId(bookLog.getLibrary().getLibraryId())
                .readDate(bookLog.getReadDate())
                .readTime(bookLog.getReadTime())
                .readParagraph(bookLog.getReadParagraph())
                .build();
    }
}
