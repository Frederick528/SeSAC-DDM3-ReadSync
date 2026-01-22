package com.ohgiraffers.backendapi.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 일반 결제 승인 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "결제 승인 요청")
public class PaymentConfirmRequest {

    @NotNull
    @Schema(description = "토스 결제 키 (paymentKey)")
    private String paymentKey;

    @NotNull
    @Schema(description = "주문 번호 (orderId)")
    private String orderId;

    @NotNull
    @Schema(description = "결제 금액 (amount)")
    private BigDecimal amount;
}
