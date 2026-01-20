package com.ohgiraffers.backendapi.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("CustomException: {}", e.getErrorCode().getMessage());
        return ErrorResponse.toResponseEntity(e);
    }

    // Toss 관련 Exception 처리
    @ExceptionHandler(TossPaymentException.class)
    protected ResponseEntity<ErrorResponse> handleTossPaymentException(
            TossPaymentException e) {
        log.error("TossPaymentException: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponse.builder()
                        .timestamp(java.time.LocalDateTime.now().toString())
                        .status(e.getStatus())
                        .error("TOSS_PAYMENT_ERROR")
                        .code(e.getCode())
                        .message(e.getMessage())
                        .detail(null)
                        .build());
    }

    // 그 외 모든 예외(500) 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception: ", e);

        return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}