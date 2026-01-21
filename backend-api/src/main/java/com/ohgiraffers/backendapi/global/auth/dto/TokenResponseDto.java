package com.ohgiraffers.backendapi.global.auth.dto; // 패키지명 확인

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
}