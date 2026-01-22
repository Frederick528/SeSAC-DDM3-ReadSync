package com.ohgiraffers.backendapi.domain.subscription.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus {
    PENDING("대기"),
    ACTIVE("이용중"),
    CANCELED("해지"),
    EXPIRED("만료"),
    PAYMENT_FAILED("결제실패");

    private final String description;
}
