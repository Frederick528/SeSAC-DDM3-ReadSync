package com.ohgiraffers.backendapi.domain.cart.controller;

import com.ohgiraffers.backendapi.domain.cart.entity.Cart;
import com.ohgiraffers.backendapi.domain.cart.service.CartService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "장바구니 API")
public class CartController {

    private final CartService cartService;

    @PostMapping
    @Operation(summary = "장바구니 담기", description = "사용자의 장바구니에 도서를 추가합니다.")
    public ResponseEntity<String> addToCart(@CurrentUserId Long userId, @RequestParam Long bookId,
            @RequestParam int quantity) {
        cartService.addItemToCart(userId, bookId, quantity);
        return ResponseEntity.ok("Item added to cart");
    }

    @GetMapping
    @Operation(summary = "장바구니 조회", description = "사용자의 장바구니 목록을 조회합니다.")
    public ResponseEntity<List<Cart>> getCartItems(@CurrentUserId Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @DeleteMapping("/{cartId}")
    @Operation(summary = "장바구니 항목 삭제", description = "장바구니에서 특정 항목을 삭제합니다.")
    public ResponseEntity<String> removeItem(@PathVariable Long cartId) {
        cartService.removeItem(cartId);
        return ResponseEntity.ok("Item removed from cart");
    }
}
