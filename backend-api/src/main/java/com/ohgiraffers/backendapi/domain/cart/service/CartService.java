package com.ohgiraffers.backendapi.domain.cart.service;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.cart.dto.CartAddRequest;
import com.ohgiraffers.backendapi.domain.cart.dto.CartResponse;
import com.ohgiraffers.backendapi.domain.cart.dto.CartUpdateRequest;
import com.ohgiraffers.backendapi.domain.cart.entity.Cart;
import com.ohgiraffers.backendapi.domain.cart.repository.CartRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 장바구니 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * 장바구니에 항목을 추가합니다. 이미 존재하는 경우 수량을 합산합니다.
     * 
     * @param userId  사용자 ID
     * @param request 추가 요청 정보
     * @return 추가된 장바구니 정보
     */
    @Transactional
    public CartResponse addToCart(Long userId, CartAddRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        // 이미 장바구니에 해당 책이 있는지 확인
        Optional<Cart> existingCartOpt = cartRepository.findByUserAndBook_BookId(user, request.getBookId());

        Cart cart;
        if (existingCartOpt.isPresent()) {
            Cart existingCart = existingCartOpt.get();
            // 존재하면 수량 합산
            cart = Cart.builder()
                    .cartId(existingCart.getCartId())
                    .user(user)
                    .book(book)
                    .quantity(existingCart.getQuantity() + request.getQuantity())
                    .createdAt(existingCart.getCreatedAt())
                    .build();
        } else {
            // 존재하지 않으면 새로 생성
            cart = Cart.builder()
                    .user(user)
                    .book(book)
                    .quantity(request.getQuantity())
                    .build();
        }
        Cart savedCart = cartRepository.save(cart);
        return convertToResponse(savedCart);
    }

    /**
     * 사용자의 장바구니 목록을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 장바구니 목록
     */
    public List<CartResponse> getCartList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return cartRepository.findByUser(user).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 장바구니 항목의 수량을 수정합니다.
     * 
     * @param userId  사용자 ID
     * @param cartId  장바구니 ID
     * @param request 수정 요청 정보
     * @return 수정된 장바구니 정보
     */
    @Transactional
    public CartResponse updateCartItem(Long userId, Long cartId, CartUpdateRequest request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        // 본인 것인지 확인
        if (!cart.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        Cart updatedCart = Cart.builder()
                .cartId(cart.getCartId())
                .user(cart.getUser())
                .book(cart.getBook())
                .quantity(request.getQuantity())
                .createdAt(cart.getCreatedAt())
                .build();

        Cart savedCart = cartRepository.save(updatedCart);
        return convertToResponse(savedCart);
    }

    /**
     * 장바구니 항목을 삭제합니다.
     * 
     * @param userId 사용자 ID
     * @param cartId 장바구니 ID
     */
    @Transactional
    public void deleteCartItem(Long userId, Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        // 본인 것인지 확인
        if (!cart.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        cartRepository.delete(cart);
    }

    /**
     * Entity를 Response DTO로 변환합니다.
     */
    private CartResponse convertToResponse(Cart cart) {
        BigDecimal bookPrice = cart.getBook().getPrice();
        BigDecimal totalPrice = bookPrice.multiply(BigDecimal.valueOf(cart.getQuantity()));

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .bookId(cart.getBook().getBookId())
                .title(cart.getBook().getTitle())
                .coverUrl(cart.getBook().getCoverUrl())
                .quantity(cart.getQuantity())
                .price(bookPrice)
                .totalPrice(totalPrice)
                .build();
    }
}
