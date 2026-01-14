package com.ohgiraffers.backendapi.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderItemStatus {
    ORDER_COMPLETED("주문완료"),
    PARTIAL_CANCELED("부분취소");

    private final String description;
}
