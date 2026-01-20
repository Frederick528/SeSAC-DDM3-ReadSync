package com.ohgiraffers.backendapi.domain.payment.service;

import com.ohgiraffers.backendapi.domain.order.entity.Order;
import com.ohgiraffers.backendapi.domain.order.enums.OrderStatus;
import com.ohgiraffers.backendapi.domain.order.repository.OrderRepository;
import com.ohgiraffers.backendapi.domain.payment.dto.TossErrorResponse;
import com.ohgiraffers.backendapi.domain.payment.dto.request.PaymentConfirmRequest;
import com.ohgiraffers.backendapi.domain.payment.entity.PaymentHistory;
import com.ohgiraffers.backendapi.domain.payment.enums.PaymentStatus;
import com.ohgiraffers.backendapi.domain.payment.enums.PgProvider;
import com.ohgiraffers.backendapi.domain.payment.enums.TransactionType;
import com.ohgiraffers.backendapi.domain.payment.repository.PaymentHistoryRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import com.ohgiraffers.backendapi.global.error.TossPaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TossPaymentService {

        private final WebClient tossPaymentWebClient;
        private final PaymentHistoryRepository paymentHistoryRepository;
        private final OrderRepository orderRepository;
        private final com.ohgiraffers.backendapi.domain.subscription.service.SubscriptionService subscriptionService;
        private final com.ohgiraffers.backendapi.domain.credit.service.CreditService creditService;
        private final com.ohgiraffers.backendapi.domain.cart.service.CartService cartService;

        public PaymentHistory confirmPayment(PaymentConfirmRequest request) {
                // 1. Verify Order exists
                Order order = orderRepository.findByOrderUid(request.getOrderId())
                                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE,
                                                "Order not found"));

                if (order.getTotalAmount().longValue() != request.getAmount()) {
                        throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "Payment amount mismatch");
                }

                // 2. Map request to map for API call
                Map<String, Object> payload = Map.of(
                                "paymentKey", request.getPaymentKey(),
                                "orderId", request.getOrderId(),
                                "amount", request.getAmount());

                // 3. Call Toss API
                Map response = tossPaymentWebClient.post()
                                .uri("/v1/payments/confirm")
                                .bodyValue(payload)
                                .retrieve()
                                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                                clientResponse -> clientResponse.bodyToMono(TossErrorResponse.class)
                                                                .flatMap(errorBody -> Mono
                                                                                .error(new TossPaymentException(
                                                                                                errorBody.getCode(),
                                                                                                errorBody.getMessage(),
                                                                                                clientResponse.statusCode()
                                                                                                                .value()))))
                                .bodyToMono(Map.class)
                                .block();

                // 4. Save PaymentHistory
                PaymentHistory history = PaymentHistory.builder()
                                .pgPaymentKey(request.getPaymentKey())
                                .amount(BigDecimal.valueOf(request.getAmount()))
                                .paymentStatus(PaymentStatus.DONE)
                                .transType(TransactionType.PAY)
                                .pgProvider(PgProvider.TOSS)
                                .order(order)
                                .build();

                PaymentHistory savedHistory = paymentHistoryRepository.save(history);

                // 5. Post-Payment Logic
                // Update Order Status
                // order.setStatus(OrderStatus.PAID); // Assuming setStatus exists or using
                // builder update in real scenario

                // Subscription Logic
                if (order.getSubscription() != null) {
                        // Activate Subscription
                        // Since SubscriptionService.createSubscription sets it to PENDING_PAYMENT, we
                        // need an 'activate' method or update status manually
                        // But implementing 'activate' in SubscriptionService is cleaner.
                        // For now, let's assume we can update it directly or call a service method if
                        // we add one.
                        com.ohgiraffers.backendapi.domain.subscription.entity.Subscription sub = order
                                        .getSubscription();
                        // We need to update sub status to ACTIVE and grant credits.
                        // This logic is best placed in SubscriptionService.activateSubscription(subId).
                        // Let's assume we add that method.
                        subscriptionService.activateSubscription(sub.getSubId());
                }

                // Cart Logic
                if (order.getCart() != null || !order.getOrderItems().isEmpty()) {
                        // If it was a cart order, clear the cart.
                        // How do we know it came from cart?
                        // createOrderFromCart added items.
                        // We can clear cart for user.
                        cartService.clearCart(order.getUser().getId());
                }

                return savedHistory;
        }

        public void cancelPayment(String paymentKey, String cancelReason) {
                PaymentHistory history = paymentHistoryRepository.findByPgPaymentKey(paymentKey)
                                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE,
                                                "Payment not found"));

                Map<String, Object> payload = Map.of("cancelReason", cancelReason);

                tossPaymentWebClient.post()
                                .uri("/v1/payments/" + paymentKey + "/cancel")
                                .bodyValue(payload)
                                .retrieve()
                                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                                clientResponse -> clientResponse.bodyToMono(TossErrorResponse.class)
                                                                .flatMap(errorBody -> Mono
                                                                                .error(new TossPaymentException(
                                                                                                errorBody.getCode(),
                                                                                                errorBody.getMessage(),
                                                                                                clientResponse.statusCode()
                                                                                                                .value()))))
                                .bodyToMono(Map.class)
                                .block();

                // Update History Logic (or create new refund history)
                // For simplicity, let's just log or update status if needed.
        }

        public String issueBillingKey(String authKey, String customerKey) {
                Map<String, String> payload = Map.of(
                                "authKey", authKey,
                                "customerKey", customerKey);

                Map response = tossPaymentWebClient.post()
                                .uri("/v1/billing/authorizations/issue")
                                .bodyValue(payload)
                                .retrieve()
                                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                                clientResponse -> clientResponse.bodyToMono(TossErrorResponse.class)
                                                                .flatMap(errorBody -> Mono
                                                                                .error(new TossPaymentException(
                                                                                                errorBody.getCode(),
                                                                                                errorBody.getMessage(),
                                                                                                clientResponse.statusCode()
                                                                                                                .value()))))
                                .bodyToMono(Map.class)
                                .block();

                return (String) response.get("billingKey");
        }

        public PaymentHistory payWithBillingKey(String billingKey, String orderId, Long amount, String orderName) {
                Map<String, Object> payload = Map.of(
                                "billingKey", billingKey,
                                "orderId", orderId,
                                "amount", amount,
                                "orderName", orderName,
                                "customerKey", "CUSTOMER_" + orderId // Example customer key
                );

                Map response = tossPaymentWebClient.post()
                                .uri("/v1/billing/" + billingKey)
                                .bodyValue(payload)
                                .retrieve()
                                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                                clientResponse -> clientResponse.bodyToMono(TossErrorResponse.class)
                                                                .flatMap(errorBody -> Mono
                                                                                .error(new TossPaymentException(
                                                                                                errorBody.getCode(),
                                                                                                errorBody.getMessage(),
                                                                                                clientResponse.statusCode()
                                                                                                                .value()))))
                                .bodyToMono(Map.class)
                                .block();

                // Save History
                // Assuming Order exists for this recurring payment, or creating one.
                // For now, let's return a simple history builder object.
                return PaymentHistory.builder()
                                .pgPaymentKey((String) response.get("paymentKey"))
                                .amount(BigDecimal.valueOf(amount))
                                .paymentStatus(PaymentStatus.DONE)
                                .transType(TransactionType.PAY)
                                .pgProvider(PgProvider.TOSS)
                                // .order(order) // Needs order linking
                                .build();
        }

        public void handleWebhook(Map<String, Object> payload) {
                String eventType = (String) payload.get("eventType");
                Map<String, Object> data = (Map<String, Object>) payload.get("data");

                if ("PAYMENT_STATUS_CHANGED".equals(eventType)) {
                        String status = (String) data.get("status");
                        String paymentKey = (String) data.get("paymentKey");

                        PaymentHistory history = paymentHistoryRepository.findByPgPaymentKey(paymentKey)
                                        .orElse(null); // Or log warning if not found

                        if (history != null) {
                                // Determine new status
                                PaymentStatus newStatus = switch (status) {
                                        case "DONE" -> PaymentStatus.DONE;
                                        case "CANCELED" -> PaymentStatus.CANCELED;
                                        default -> history.getPaymentStatus();
                                };

                                // Update status (Assuming setter exists or we use builder to recreate/update if
                                // immutable)
                                // history.setPaymentStatus(newStatus); // Need setter in Entity or update
                                // method
                        }
                }
        }
}
