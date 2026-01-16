package com.ohgiraffers.backendapi.domain.book.dto;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.enums.ViewPermission;
import com.ohgiraffers.backendapi.domain.category.entity.Category;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDTO {

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

    public Book toEntity(Category category) {
        return Book.builder()
                .category(category)
                .title(this.title)
                .author(this.author)
                .isAdultOnly(this.isAdultOnly)
                .summary(this.summary)
                .publisher(this.publisher)
                .publishedDate(this.publishedDate)
                .coverUrl(this.coverUrl)
                .viewPermission(this.viewPermission)
                .price(this.price)
                .language(this.language)
                .build();
    }

}
