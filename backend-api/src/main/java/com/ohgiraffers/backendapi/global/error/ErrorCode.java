package com.ohgiraffers.backendapi.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 유저 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),

    // 친구 요청 관련 에러
    CANNOT_REQUEST_TO_SELF(HttpStatus.BAD_REQUEST, "F001", "자기 자신에게는 친구 요청을 보낼 수 없습니다."),
    ADDRESSEE_NOT_FOUND(HttpStatus.NOT_FOUND, "F002", "대상자를 찾을 수 없습니다."),
    ALREADY_FRIENDS(HttpStatus.BAD_REQUEST, "F003", "이미 친구 관계입니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "F004", "존재하지 않는 친구 요청입니다."),
    NO_AUTHORITY_TO_UPDATE(HttpStatus.FORBIDDEN, "F005", "해당 작업에 대한 권한이 없습니다."),
    INVALID_REQUEST_STATUS(HttpStatus.BAD_REQUEST, "F006", "유효하지 않은 요청 상태입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
