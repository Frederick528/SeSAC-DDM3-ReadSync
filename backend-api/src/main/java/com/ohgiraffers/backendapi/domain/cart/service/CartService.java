package com.ohgiraffers.backendapi.domain.cart.service;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.cart.entity.Cart;
import com.ohgiraffers.backendapi.domain.cart.repository.CartRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public void addItemToCart(Long userId, Long bookId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        Cart cart = cartRepository.findByUser_IdAndBook_BookId(userId, bookId)
                .map(existingCart -> {
                    existingCart.updateQuantity(existingCart.getQuantity() + quantity);
                    return existingCart;
                })
                .orElseGet(() -> Cart.builder()
                        .user(user)
                        .book(book)
                        .quantity(quantity)
                        .build());

        cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public List<Cart> getCartItems(Long userId) {
        return cartRepository.findByUser_Id(userId);
    }

    @Transactional
    public void removeItem(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteByUser_Id(userId);
    }
}
