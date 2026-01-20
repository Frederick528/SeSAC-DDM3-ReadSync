package com.ohgiraffers.backendapi.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 빌링키 발급 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "빌링키 발급 요청")
public class BillingKeyRequest {

    @NotNull
    @Schema(description = "토스 인증 키 (authKey)")
    private String authKey;

    @NotNull
    @Schema(description = "고객 키 (customerKey)")
    private String customerKey;
}
