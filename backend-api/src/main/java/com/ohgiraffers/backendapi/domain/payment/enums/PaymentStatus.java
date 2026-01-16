package com.ohgiraffers.backendapi.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    DONE("결제 완료"),
    CANCELED("취소됨"),
    FAILED("실패함");

    private final String description;
}
