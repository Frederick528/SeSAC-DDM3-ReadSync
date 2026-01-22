package com.ohgiraffers.backendapi.domain.order.service;

import com.ohgiraffers.backendapi.domain.cart.entity.Cart;
import com.ohgiraffers.backendapi.domain.cart.repository.CartRepository;
import com.ohgiraffers.backendapi.domain.order.entity.Order;
import com.ohgiraffers.backendapi.domain.order.entity.OrderItem;
import com.ohgiraffers.backendapi.domain.order.enums.OrderItemStatus;
import com.ohgiraffers.backendapi.domain.order.enums.OrderStatus;
import com.ohgiraffers.backendapi.domain.order.repository.OrderItemRepository;
import com.ohgiraffers.backendapi.domain.order.repository.OrderRepository;
import com.ohgiraffers.backendapi.domain.payment.dto.PaymentConfirmRequest;
import com.ohgiraffers.backendapi.domain.payment.entity.PaymentHistory;
import com.ohgiraffers.backendapi.domain.payment.enums.PaymentStatus;
import com.ohgiraffers.backendapi.domain.payment.enums.PgProvider;
import com.ohgiraffers.backendapi.domain.payment.enums.TransactionType;
import com.ohgiraffers.backendapi.domain.payment.repository.PaymentHistoryRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 주문 처리 비즈니스 로직을 담당하는 서비스 클래스입니다.
 */
@Service
@lombok.extern.slf4j.Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    /**
     * 장바구니 항목들을 기반으로 주문을 생성합니다. (일반 결제 성공 후 호출)
     */
    @Transactional
    public Order createOrderFromPayment(Long userId, PaymentConfirmRequest request, Map<String, Object> paymentResult) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            // 결제는 성공했는데 장바구니가 비어있는 경우?
            // 이미 결제가 되었으므로 로그를 남기고 빈 주문이라도 생성하거나 예외 처리
            // 여기서는 예외 처리보다는 주문 기록 생성에 집중
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 결제 금액 검증
        if (request.getAmount().compareTo(totalAmount) != 0) {
            log.error("결제 금액 불일치 발생! 요청 금액: {}, 실제 장바구니 합계: {}", request.getAmount(), totalAmount);
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        Order order = Order.builder()
                .user(user)
                .paymentMethod(null) // 일반 결제는 등록된 결제 수단이 없을 수 있음
                .orderUid(request.getOrderId())
                .orderName((String) paymentResult.get("orderName")) // Toss 응답에서 orderName 추출
                .totalAmount(request.getAmount())
                .status(OrderStatus.COMPLETED)
                .build();

        Order savedOrder = orderRepository.save(order);

        // 결제 이력 저장
        String paymentKey = (String) paymentResult.get("paymentKey");
        String statusStr = (String) paymentResult.get("status");
        PaymentStatus paymentStatus = "DONE".equals(statusStr) ? PaymentStatus.DONE : PaymentStatus.FAILED; // 단순 매핑,
                                                                                                            // 필요시 확장

        // receipt 객체에서 url 추출
        String receiptUrl = null;
        if (paymentResult.get("receipt") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> receipt = (Map<String, Object>) paymentResult.get("receipt");
            receiptUrl = (String) receipt.get("url");
        }

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .pgPaymentKey(paymentKey)
                .amount(request.getAmount())
                .paymentStatus(paymentStatus)
                .transType(TransactionType.PAY)
                .pgProvider(PgProvider.TOSS)
                .receiptUrl(receiptUrl)
                .order(savedOrder)
                .build();

        paymentHistoryRepository.save(paymentHistory);

        // 주문 상세 항목 생성
        for (Cart item : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .book(item.getBook())
                    .order(savedOrder)
                    .snapshotPrice(item.getBook().getPrice())
                    .quantity(item.getQuantity())
                    .status(OrderItemStatus.ORDER_COMPLETED)
                    .build();
            orderItemRepository.save(orderItem);
        }

        // 장바구니 비우기
        cartRepository.deleteByUser(user);

        return savedOrder;
    }
}
