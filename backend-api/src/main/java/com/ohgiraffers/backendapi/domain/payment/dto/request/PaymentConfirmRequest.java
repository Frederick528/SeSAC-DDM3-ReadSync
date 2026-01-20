package com.ohgiraffers.backendapi.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "결제 승인 요청 DTO")
public class PaymentConfirmRequest {
    @NotBlank
    @Schema(description = "결제 키 (Toss Payments 제공)", example = "test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq")
    private String paymentKey;

    @NotBlank
    @Schema(description = "주문 ID", example = "order_uid_1234")
    private String orderId;

    @NotNull
    @Schema(description = "결제 금액", example = "10000")
    private Long amount;
}
