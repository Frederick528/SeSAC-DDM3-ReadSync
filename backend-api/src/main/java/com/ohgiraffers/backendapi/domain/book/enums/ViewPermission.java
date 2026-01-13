package com.ohgiraffers.backendapi.domain.book.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ViewPermission {
    FREE("무료 이용"),
    PREMIUM("구독자 전용"),
    ADMIN("관리자 전용");

    private final String description;
}
