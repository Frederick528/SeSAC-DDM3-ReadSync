package com.ohgiraffers.backendapi.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 컨트롤러의 예외를 감시
@Slf4j
public class GlobalExceptionHandler {

//    // 우리가 직접 정의한 BusinessException이 발생했을 때
//    @ExceptionHandler(BusinessException.class)
//    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
//        log.error("BusinessException: {}", e.getErrorCode().getMessage());
//        return ErrorResponse.toResponseEntity(e.getErrorCode());
//    }

// [수정] BusinessException -> CustomException으로 변경 (만들어두신 파일이 CustomException으로 되어있기 때문)
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("CustomException: {}", e.getErrorCode().getMessage());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    // 그 외 예상치 못한 모든 예외(500 에러 등)가 발생했을 때
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception: ", e);
        return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}