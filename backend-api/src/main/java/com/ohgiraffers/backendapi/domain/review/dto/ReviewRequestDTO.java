package com.ohgiraffers.backendapi.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDTO {
    private Long bookId;    // 생성 시에만 필요
    private Integer rating;
    private String content;
    private Boolean isSpoiler;
}
