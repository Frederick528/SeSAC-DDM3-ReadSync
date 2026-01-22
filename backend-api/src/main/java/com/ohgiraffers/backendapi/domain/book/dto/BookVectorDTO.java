package com.ohgiraffers.backendapi.domain.book.dto;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookVectorDTO {
    private Long bookId;
    private String title;
    private String author;
//    private Double score;

    public static BookVectorDTO from(Book book) {
        return BookVectorDTO.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .build();
    }
}