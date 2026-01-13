package com.ohgiraffers.backendapi.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    ACTIVE("활동 중"),     // 정상 이용 가능
    WITHDRAWN("탈퇴"),    // 자진 탈퇴 (Soft Delete 대상)
    BANNED("정지");       // 운영 정책 위반으로 인한 차단

    private final String description;
}