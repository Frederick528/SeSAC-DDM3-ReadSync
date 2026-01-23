package com.ohgiraffers.backendapi.domain.cart.controller;

import com.ohgiraffers.backendapi.domain.cart.dto.CartAddRequest;
import com.ohgiraffers.backendapi.domain.cart.dto.CartResponse;
import com.ohgiraffers.backendapi.domain.cart.dto.CartUpdateRequest;
import com.ohgiraffers.backendapi.domain.cart.service.CartService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 장바구니 관련 API를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Tag(name = "Cart (장바구니)", description = "장바구니 담기, 조회, 수정, 삭제 API")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 담기", description = "도서를 장바구니에 추가합니다.")
    @PostMapping
    public ResponseEntity<CartResponse> addToCart(
            @CurrentUserId Long userId,
            @Valid @RequestBody CartAddRequest request) {
        CartResponse response = cartService.addToCart(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "장바구니 목록 조회", description = "현재 로그인한 사용자의 장바구니 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CartResponse>> getCartList(
            @CurrentUserId Long userId) {
        List<CartResponse> response = cartService.getCartList(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "장바구니 수량 수정", description = "장바구니에 담긴 도서의 수량을 수정합니다.")
    @PatchMapping("/{cartId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @CurrentUserId Long userId,
            @PathVariable Long cartId,
            @Valid @RequestBody CartUpdateRequest request) {
        CartResponse response = cartService.updateCartItem(userId, cartId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "장바구니 항목 삭제", description = "장바구니에서 특정 항목을 삭제합니다.")
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCartItem(
            @CurrentUserId Long userId,
            @PathVariable Long cartId) {
        cartService.deleteCartItem(userId, cartId);
        return ResponseEntity.noContent().build();
    }
}
