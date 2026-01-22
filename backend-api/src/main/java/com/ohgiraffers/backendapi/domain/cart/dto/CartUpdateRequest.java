package com.ohgiraffers.backendapi.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 장바구니 수량 수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "장바구니 수량 수정 요청")
public class CartUpdateRequest {

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    @Schema(description = "변경할 수량", example = "5")
    private Integer quantity;
}
