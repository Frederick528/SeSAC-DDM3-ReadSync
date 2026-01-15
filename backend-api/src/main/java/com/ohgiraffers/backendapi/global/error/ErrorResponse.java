package com.ohgiraffers.backendapi.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;

    // ErrorResponse에 toResponseEntity가 없어서 생성함. (있으면 이거 지우고 GlobalExceptionHandler의 handleCustomException 수정해주세요.)
    // 에러 코드를 받아서 ResponseEntity로 변환해주는 정적 메서드
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}