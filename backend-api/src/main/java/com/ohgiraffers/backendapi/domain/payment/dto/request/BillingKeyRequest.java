package com.ohgiraffers.backendapi.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "빌링키 발급 요청 DTO")
public class BillingKeyRequest {
    @NotBlank
    @Schema(description = "인증 키 (카드 등록 후 발급받은 키)", example = "auth_key_1234")
    private String authKey;

    @NotBlank
    @Schema(description = "고객 키 (가맹점에서 생성한 고객 식별자)", example = "customer_1234")
    private String customerKey;
}
