package com.ohgiraffers.backendapi.domain.payment.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "거래 유형")
public enum TransactionType {
    PAY("결제"),
    REFUND("환불");

    private final String description;
}
