package com.ohgiraffers.backendapi.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VisibilityStatus {
    ACTIVE("정상노출"),
    BLINDED("신고누적으로가려짐"),
    SUSPENDED("강제비노출"),
    DELETED("작성자에의한삭제");

    private final String description;
}
