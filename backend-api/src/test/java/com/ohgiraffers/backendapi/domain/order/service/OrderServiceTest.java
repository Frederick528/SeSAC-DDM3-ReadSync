package com.ohgiraffers.backendapi.domain.order.service;

import com.ohgiraffers.backendapi.domain.cart.entity.Cart;
import com.ohgiraffers.backendapi.domain.cart.service.CartService;
import com.ohgiraffers.backendapi.domain.order.entity.Order;
import com.ohgiraffers.backendapi.domain.order.enums.OrderStatus;
import com.ohgiraffers.backendapi.domain.order.repository.OrderRepository;
import com.ohgiraffers.backendapi.domain.payment.entity.PaymentMethod;
import com.ohgiraffers.backendapi.domain.payment.repository.PaymentMethodRepository;
import com.ohgiraffers.backendapi.domain.subscription.entity.Subscription;
import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionPlan;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.domain.book.entity.Book; // Added import
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("장바구니에서 주문 생성 성공")
    void createOrderFromCart_Success() {
        // given
        Long userId = 1L;
        Long methodId = 1L;
        User user = User.builder().id(userId).build();
        PaymentMethod paymentMethod = PaymentMethod.builder().methodId(methodId).build();
        Book book = Book.builder().price(BigDecimal.valueOf(10000)).build();
        Cart cartItem = Cart.builder().book(book).quantity(2).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(paymentMethodRepository.findById(methodId)).thenReturn(Optional.of(paymentMethod));
        when(cartService.getCartItems(userId)).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order order = orderService.createOrderFromCart(userId, methodId);

        // then
        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(BigDecimal.valueOf(20000), order.getTotalAmount());
        assertEquals(1, order.getOrderItems().size());
    }

    @Test
    @DisplayName("구독 주문 생성 성공")
    void createOrderFromSubscription_Success() {
        // given
        Long userId = 1L;
        Long methodId = 1L;
        User user = User.builder().id(userId).build();
        PaymentMethod paymentMethod = PaymentMethod.builder().methodId(methodId).build();
        Subscription subscription = Subscription.builder()
                .planName(SubscriptionPlan.BASIC)
                .price(BigDecimal.valueOf(9900))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(paymentMethodRepository.findById(methodId)).thenReturn(Optional.of(paymentMethod));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order order = orderService.createOrderFromSubscription(userId, subscription, methodId);

        // then
        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(BigDecimal.valueOf(9900), order.getTotalAmount());
        assertEquals(subscription, order.getSubscription());
    }
}
