package com.ohgiraffers.backendapi.domain.order.service;

import com.ohgiraffers.backendapi.domain.cart.entity.Cart;
import com.ohgiraffers.backendapi.domain.cart.service.CartService;
import com.ohgiraffers.backendapi.domain.order.entity.Order;
import com.ohgiraffers.backendapi.domain.order.entity.OrderItem;
import com.ohgiraffers.backendapi.domain.order.enums.OrderStatus;
import com.ohgiraffers.backendapi.domain.order.repository.OrderRepository;
import com.ohgiraffers.backendapi.domain.payment.entity.PaymentMethod;
import com.ohgiraffers.backendapi.domain.payment.repository.PaymentMethodRepository;
import com.ohgiraffers.backendapi.domain.subscription.entity.Subscription;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Transactional
    public Order createOrderFromCart(Long userId, Long methodId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        List<Cart> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .paymentMethod(paymentMethod)
                .orderUid(UUID.randomUUID().toString())
                .orderName("Cart Order") // Can be improved to list book names
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .cart(cartItems.get(0)) // Using first cart item's cart link, logic might vary if Cart is per-item.
                                        // Wait, Cart entity IS an item. So 'cart_id' in Order implies linking to ONE
                                        // cart item?
                                        // The ERD link is 1:N or N:1. Usually Order links to a "Cart" session, but here
                                        // Cart is items.
                                        // Let's assume we link to the first cart item's ID or leave it null if it's a
                                        // bulk order or handle differently.
                                        // Actually, if we clear the cart after order, this link is historical.
                                        // Let's create OrderItems for each Cart item.
                .build();

        for (Cart cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .book(cartItem.getBook())
                    .snapshotPrice(cartItem.getBook().getPrice())
                    .quantity(cartItem.getQuantity())
                    .status("READY")
                    .build();
            order.addOrderItem(orderItem);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrderFromSubscription(Long userId, Subscription subscription, Long methodId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        Order order = Order.builder()
                .user(user)
                .paymentMethod(paymentMethod)
                .orderUid(UUID.randomUUID().toString())
                .orderName(subscription.getPlanName().name() + " Subscription")
                .totalAmount(subscription.getPrice())
                .status(OrderStatus.PENDING)
                .subscription(subscription)
                .build();

        return orderRepository.save(order);
    }
}
