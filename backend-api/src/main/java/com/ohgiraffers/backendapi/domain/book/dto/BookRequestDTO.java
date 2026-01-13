package com.ohgiraffers.backendapi.domain.book.dto;

import com.ohgiraffers.backendapi.domain.book.enums.ViewPermission;
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
}
