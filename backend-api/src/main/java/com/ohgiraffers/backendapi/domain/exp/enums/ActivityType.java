package com.ohgiraffers.backendapi.domain.exp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityType {
    READ_BOOK("책 읽기"),
    WRITE_REVIEW("리뷰 작성"),
    DAILY_ATTENDANCE("출석 체크");

    private final String description;
}