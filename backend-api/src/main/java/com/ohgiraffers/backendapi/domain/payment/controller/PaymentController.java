package com.ohgiraffers.backendapi.domain.payment.controller;

import com.ohgiraffers.backendapi.domain.payment.dto.request.PaymentCancelRequest;
import com.ohgiraffers.backendapi.domain.payment.dto.request.PaymentConfirmRequest;
import com.ohgiraffers.backendapi.domain.payment.entity.PaymentHistory;
import com.ohgiraffers.backendapi.domain.payment.service.TossPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final TossPaymentService tossPaymentService;

    @Operation(summary = "결제 승인", description = "토스페이먼츠 결제 승인을 요청합니다.")
    @PostMapping("/confirm")
    public ResponseEntity<PaymentHistory> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        PaymentHistory history = tossPaymentService.confirmPayment(request);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "결제 취소", description = "이미 완료된 결제를 취소(환불) 요청합니다.")
    @PostMapping("/{paymentKey}/cancel")
    public ResponseEntity<Void> cancelPayment(@PathVariable String paymentKey,
            @RequestBody PaymentCancelRequest request) {
        tossPaymentService.cancelPayment(paymentKey, request.getCancelReason());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "빌링키 발급", description = "정기 결제를 위한 빌링키를 발급받습니다.")
    @PostMapping("/billing-key")
    public ResponseEntity<String> issueBillingKey(
            @RequestBody com.ohgiraffers.backendapi.domain.payment.dto.request.BillingKeyRequest request) {
        String billingKey = tossPaymentService.issueBillingKey(request.getAuthKey(), request.getCustomerKey());
        return ResponseEntity.ok(billingKey);
    }
}
