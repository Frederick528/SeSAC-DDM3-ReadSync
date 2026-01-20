package com.ohgiraffers.backendapi.domain.subscription.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@RequiredArgsConstructor
@Schema(description = "구독 플랜")
public enum SubscriptionPlan {

    @Schema(description = "베이직 플랜 (9,900원, 100 크레딧)")
    BASIC("BASIC", 9900L, 100),

    @Schema(description = "스탠다드 플랜 (19,900원, 300 크레딧)")
    STANDARD("STANDARD", 19900L, 300),

    @Schema(description = "프리미엄 플랜 (33,000원, 600 크레딧)")
    PREMIUM("PREMIUM", 33000L, 600);

    private final String planName;
    private final Long price;
    private final int credits;
}
