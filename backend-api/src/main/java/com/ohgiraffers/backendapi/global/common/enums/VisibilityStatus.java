package com.ohgiraffers.backendapi.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VisibilityStatus {
    ACTIVE("정상 노출"),
    BLINDED("신고 누적으로 가려짐"),
    SUSPENDED("강제 비노출"),
    DELETED("작성자에의한 삭제");

    private final String description;
}
