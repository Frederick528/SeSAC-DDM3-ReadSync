package com.ohgiraffers.backendapi.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 완료 정보 응답 DTO
 */
@Getter
@Builder
@Schema(description = "결제 완료 정보 응답")
public class PaymentResponse {

    @Schema(description = "결제 키")
    private String paymentKey;

    @Schema(description = "주문 번호")
    private String orderId;

    @Schema(description = "결제 금액")
    private BigDecimal amount;

    @Schema(description = "결제 상태")
    private String status;

    @Schema(description = "결제 일시")
    private LocalDateTime requestedAt;

    @Schema(description = "영수증 URL")
    private String receiptUrl;
}
