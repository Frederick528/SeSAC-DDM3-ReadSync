package com.ohgiraffers.backendapi.domain.userbook.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReadingStatus {
    BEFORE_READING("읽기 전"),
    READING("읽는 중"),
    COMPLETED("다 읽음");

    private final String description;
}
