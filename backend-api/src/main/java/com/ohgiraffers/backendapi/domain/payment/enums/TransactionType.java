package com.ohgiraffers.backendapi.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    PAY("결제"),
    REFUND("환불");

    private final String description;
}
