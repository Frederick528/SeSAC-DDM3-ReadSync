package com.ohgiraffers.backendapi.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PgProvider {
    TOSS("토스"),
    KAKAO("카카오페이"),
    NAVER("네이버페이");

    private final String description;
}