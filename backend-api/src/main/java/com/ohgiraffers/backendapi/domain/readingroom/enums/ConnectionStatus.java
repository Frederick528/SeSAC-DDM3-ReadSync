package com.ohgiraffers.backendapi.domain.readingroom.enums;

public enum ConnectionStatus {
    ACTIVE,         // 접속 중
    EXITED,         // 나감 (자발적)
    DISCONNECTED    // 끊김 (네트워크 오류 등 - 재입장 가능)
}
