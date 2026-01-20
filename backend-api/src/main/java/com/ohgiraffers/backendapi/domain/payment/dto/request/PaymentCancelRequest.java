package com.ohgiraffers.backendapi.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "결제 취소 요청 DTO")
public class PaymentCancelRequest {
    @NotBlank
    @Schema(description = "취소 사유", example = "단순 변심 요청")
    private String cancelReason;

    @Schema(description = "취소 금액 (부분 환불 시 필수)", example = "5000", nullable = true)
    private Integer cancelAmount; // Optional for partial refund
}
