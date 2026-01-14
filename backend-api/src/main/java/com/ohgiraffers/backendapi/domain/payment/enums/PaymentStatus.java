package com.ohgiraffers.backendapi.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    // 1. 결제 시작 전/진행 중
    READY("결제 대기"),           // 사용자가 결제창에 진입했으나 아직 인증 전
    IN_PROGRESS("결제 요청 중"),    // 사용자가 인증을 마치고 승인을 기다리는 상태

    // 2. 결제 성공/대기 (가상계좌)
    WAITING_FOR_DEPOSIT("입금 대기"), // 가상계좌 발급 후 입금 전 상태
    DONE("결제 완료"),            // 결제 승인이 정상적으로 완료된 상태

    // 3. 결제 실패/중단
    CANCELED("결제 취소"),        // 승인된 결제가 전액 취소된 상태
    PARTIAL_CANCELED("부분 취소"), // 결제 금액 중 일부만 취소된 상태
    ABORTED("결제 중단"),         // 사용자가 결제창을 닫거나, 승인 전 중단된 상태
    EXPIRED("유효시간 만료");      // 가상계좌 입금 기한 만료 등

    private final String description;
}