package com.ohgiraffers.backendapi.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "토스 페이먼츠 에러 응답 DTO")
public class TossErrorResponse {
    @Schema(description = "에러 코드", example = "INVALID_API_KEY")
    private String code;

    @Schema(description = "에러 메시지", example = "잘못된 API 키입니다.")
    private String message;
}
