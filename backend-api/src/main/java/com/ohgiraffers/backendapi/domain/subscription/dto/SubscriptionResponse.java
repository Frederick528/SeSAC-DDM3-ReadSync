package com.ohgiraffers.backendapi.domain.subscription.dto;

import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 구독 정보 응답 DTO
 */
@Getter
@Builder
public class SubscriptionResponse {
    private Long subId;
    private String planName;
    private BigDecimal price;
    private SubscriptionStatus status;
    private LocalDateTime nextBillingDate;
    private LocalDateTime startedAt;
}
