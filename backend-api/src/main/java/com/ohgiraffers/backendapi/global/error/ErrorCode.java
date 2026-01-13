package com.ohgiraffers.backendapi.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    CANNOT_REQUEST_TO_SELF(HttpStatus.BAD_REQUEST, "F001", "자기 자신에게는 친구 요청을 보낼 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
