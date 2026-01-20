package com.ohgiraffers.backendapi.domain.payment.controller;

import com.ohgiraffers.backendapi.domain.payment.dto.BillingKeyRequest;
import com.ohgiraffers.backendapi.domain.payment.dto.PaymentConfirmRequest;
import com.ohgiraffers.backendapi.domain.payment.service.PaymentService;
import com.ohgiraffers.backendapi.domain.payment.service.TossPaymentService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 결제 관련 API를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment (결제)", description = "결제 승인 및 결제 수단 관리 API")
public class PaymentController {

    private final PaymentService paymentService;
    private final TossPaymentService tossPaymentService;
    private final com.ohgiraffers.backendapi.domain.order.service.OrderService orderService;

    @Operation(summary = "일반 결제 승인", description = "토스 페이먼츠 결제창 완료 후 백엔드 서버에서 승인을 완료합니다.")
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @CurrentUserId Long userId,
            @Valid @RequestBody PaymentConfirmRequest request) {
        Map<String, Object> result = tossPaymentService.confirmPayment(request);

        // 결제 승인 후 주문 생성
        orderService.createOrderFromPayment(userId, request, result);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "빌링키 등록", description = "정기 결제를 위한 카드 정보를 등록하고 빌링키를 발급받습니다.")
    @PostMapping("/billing-key")
    public ResponseEntity<Void> registerBillingKey(
            @CurrentUserId Long userId,
            @Valid @RequestBody BillingKeyRequest request) {
        paymentService.registerPaymentMethod(userId, request);
        return ResponseEntity.ok().build();
    }
}
