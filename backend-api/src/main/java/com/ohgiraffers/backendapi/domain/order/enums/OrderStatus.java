package com.ohgiraffers.backendapi.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("주문 대기"),
    COMPLETED("주문 완료"),
    CANCELED("주문 취소"),
    FAILED("주문 실패");

    private final String description;
}
