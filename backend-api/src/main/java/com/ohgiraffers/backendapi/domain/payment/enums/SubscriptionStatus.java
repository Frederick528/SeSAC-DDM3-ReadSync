package com.ohgiraffers.backendapi.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus {
    ACTIVE("이용 중"),             // 결제 성공 및 서비스 이용 가능 상태
    CANCELED("해지 신청"),         // 다음 결제일에 해지 예정 (남은 기간은 이용 가능)
    EXPIRED("만료됨"),             // 이용 기간 종료 (재결제 안됨)
    PAYMENT_FAILED("결제 실패"),   // 잔액 부족 등으로 자동 결제 실패 (유예 기간 등 처리)
    PAUSED("일시 정지");           // 사용자에 의한 일시 정지 (옵션)

    private final String description;
}
