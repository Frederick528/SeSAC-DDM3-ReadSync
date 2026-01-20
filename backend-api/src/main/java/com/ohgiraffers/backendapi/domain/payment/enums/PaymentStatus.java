package com.ohgiraffers.backendapi.domain.payment.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "결제 상태")
public enum PaymentStatus {
    DONE("결제 완료"),
    CANCELED("취소됨"),
    FAILED("실패함");

    private final String description;
}
