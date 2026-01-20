package com.ohgiraffers.backendapi.domain.cart.service;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.book.repository.BookRepository;
import com.ohgiraffers.backendapi.domain.cart.entity.Cart;
import com.ohgiraffers.backendapi.domain.cart.repository.CartRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니 담기 - 이미 존재하는 상품은 수량 증가")
    void addItemToCart_UpdateQuantity() {
        // given
        Long userId = 1L;
        Long bookId = 1L;
        int quantity = 2;

        User user = User.builder().id(userId).build();
        Book book = Book.builder().bookId(bookId).build();
        Cart existingCart = Cart.builder().user(user).book(book).quantity(1).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartRepository.findByUser_IdAndBook_BookId(userId, bookId)).thenReturn(Optional.of(existingCart));

        // when
        cartService.addItemToCart(userId, bookId, quantity);

        // then
        assertEquals(3, existingCart.getQuantity());
        verify(cartRepository, times(1)).save(existingCart);
    }

    @Test
    @DisplayName("장바구니 담기 - 새로운 상품 추가")
    void addItemToCart_NewItem() {
        // given
        Long userId = 1L;
        Long bookId = 1L;
        int quantity = 1;

        User user = User.builder().id(userId).build();
        Book book = Book.builder().bookId(bookId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartRepository.findByUser_IdAndBook_BookId(userId, bookId)).thenReturn(Optional.empty());

        // when
        cartService.addItemToCart(userId, bookId, quantity);

        // then
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}
