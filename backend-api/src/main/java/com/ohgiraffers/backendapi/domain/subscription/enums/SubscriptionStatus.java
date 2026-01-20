package com.ohgiraffers.backendapi.domain.subscription.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "구독 상태")
public enum SubscriptionStatus {
    @Schema(description = "활성")
    ACTIVE,
    @Schema(description = "만료")
    EXPIRED,
    @Schema(description = "해지 예약(다음 결제일까지만 유효)")
    CANCELED,
    @Schema(description = "결제 대기")
    PENDING_PAYMENT
}
