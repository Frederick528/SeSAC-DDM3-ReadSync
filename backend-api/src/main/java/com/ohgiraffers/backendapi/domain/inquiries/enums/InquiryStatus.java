package com.ohgiraffers.backendapi.domain.inquiries.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryStatus {

    WAIT("대기"),
    ANSWERED("답변 완료"),
    CLOSED("종료");

    private final String description;
}
