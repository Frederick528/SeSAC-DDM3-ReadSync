package com.ohgiraffers.backendapi.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 장바구니 응답 DTO
 */
@Getter
@Builder
@Schema(description = "장바구니 상세 정보 응답")
public class CartResponse {

    @Schema(description = "장바구니 ID", example = "1")
    private Long cartId;

    @Schema(description = "도서 ID", example = "1")
    private Long bookId;

    @Schema(description = "도서 제목", example = "샘플 북")
    private String title;

    @Schema(description = "도서 커버 URL")
    private String coverUrl;

    @Schema(description = "수량", example = "2")
    private Integer quantity;

    @Schema(description = "도서 개별 가격", example = "15000")
    private BigDecimal price;

    @Schema(description = "총 가격 (가격 x 수량)", example = "30000")
    private BigDecimal totalPrice;
}
