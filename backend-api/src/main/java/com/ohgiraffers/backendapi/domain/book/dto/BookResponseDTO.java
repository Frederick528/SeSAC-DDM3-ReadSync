package com.ohgiraffers.backendapi.domain.book.dto;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.enums.ViewPermission;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDTO {

    private Long bookId;
    private Long categoryId;
    private String title;
    private String author;
    private Boolean isAdultOnly;
    private String summary;
    private String publisher;
    private LocalDate publishedDate;
    private String coverUrl;
    private ViewPermission viewPermission;
    private BigDecimal price;
    private String language;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BookResponseDTO from(Book book) {
        return BookResponseDTO.builder()
                .bookId(book.getBookId())
                .categoryId(book.getCategory().getCategoryId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isAdultOnly(book.getIsAdultOnly())
                .summary(book.getSummary())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate())
                .coverUrl(book.getCoverUrl())
                .viewPermission(book.getViewPermission())
                .price(book.getPrice())
                .language(book.getLanguage())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}

