package com.ohgiraffers.backendapi.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C003", "잘못된 타입입니다."),

    // 인증/권환
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A003", "아이디 또는 비밀번호가 일치하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A004", "접근 권한이 없습니다."),

    // 유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "U002", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 사용 중인 이메일입니다."),

    // 댓글
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "해당 댓글을 찾을 수 없습니다."),

    // 리뷰
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "해당 리뷰를 찾을 수 없습니다.");

    // 친구
    CANNOT_REQUEST_TO_SELF(HttpStatus.BAD_REQUEST, "F001", "자기 자신에게는 친구 요청을 보낼 수 없습니다."),
    ADDRESSEE_NOT_FOUND(HttpStatus.NOT_FOUND, "F002", "대상자를 찾을 수 없습니다."),
    ALREADY_FRIENDS(HttpStatus.BAD_REQUEST, "F003", "이미 친구 관계입니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "F004", "존재하지 않는 친구 요청입니다."),
    NO_AUTHORITY_TO_UPDATE(HttpStatus.FORBIDDEN, "F005", "해당 작업에 대한 권한이 없습니다."),
    INVALID_REQUEST_STATUS(HttpStatus.BAD_REQUEST, "F006", "유효하지 않은 요청 상태입니다.");

    private final HttpStatus status;
    private final String message;
    private final String code;
}

/*
 결과: "해당 유저를 찾을 수 없습니다."
 throw new CustomException(ErrorCode.USER_NOT_FOUND);

 Long userId = 100L;
 // 결과: "해당 유저를 찾을 수 없습니다. (ID: 100)"
 throw new CustomException(ErrorCode.USER_NOT_FOUND, "ID: " + userId);

* */
