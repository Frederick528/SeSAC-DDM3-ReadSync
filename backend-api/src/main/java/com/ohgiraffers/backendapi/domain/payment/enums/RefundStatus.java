package com.ohgiraffers.backendapi.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RefundStatus {
    NONE("환불 없음"),             // 정상 결제 상태 (환불 이력 없음)
    PENDING("환불 처리 중"),       // 환불 요청이 접수되어 처리 중인 상태 (은행 점검 시간 등)
    FAILED("환불 실패"),           // 잔액 부족, 계좌 오류 등으로 환불 실패
    PARTIAL_FAILED("부분 환불 실패"), // 부분 환불 시도 중 일부 실패
    COMPLETED("환불 완료");        // 환불 처리가 정상적으로 끝난 상태

    private final String description;
}
