package com.ohgiraffers.backendapi.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportProcessStatus {
    PENDING("처리중"),
    ACCEPTED("처리됨"),
    REJECTED("거절됨");


    private final String description;
}
