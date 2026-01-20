package com.ohgiraffers.backendapi.domain.payment.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "PG사 제공자")
public enum PgProvider {
    TOSS("토스"),
    KAKAO("카카오페이"),
    NAVER("네이버페이");

    private final String description;
}