package com.ohgiraffers.backendapi.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 장바구니 추가 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "장바구니 추가 요청")
public class CartAddRequest {

    @NotNull(message = "도서 ID는 필수입니다.")
    @Schema(description = "도서 ID", example = "1")
    private Long bookId;

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    @Schema(description = "수량", example = "1")
    private Integer quantity;
}
