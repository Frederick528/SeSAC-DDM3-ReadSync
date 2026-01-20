package com.ohgiraffers.backendapi.global.error;

import lombok.Getter;

@Getter
public class TossPaymentException extends RuntimeException {
    private final String code;
    private final String message;
    private final int status;

    public TossPaymentException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
