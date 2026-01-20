package com.ohgiraffers.backendapi.domain.subscription.controller;

import com.ohgiraffers.backendapi.domain.subscription.dto.SubscriptionResponse;
import com.ohgiraffers.backendapi.domain.subscription.service.SubscriptionService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 구독 관련 API를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscription (구독)", description = "정기 결제 구독 신청, 해지 API")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "구독 신청", description = "정기 결제 플랜에 가입합니다. 등록된 결제 수단으로 첫 결제가 진행됩니다.")
    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(
            @CurrentUserId Long userId,
            @RequestParam String planName,
            @RequestParam BigDecimal price) {
        SubscriptionResponse response = subscriptionService.subscribe(userId, planName, price);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 구독 정보 조회", description = "현재 이용 중인 구독 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponse> getMySubscription(
            @CurrentUserId Long userId) {
        SubscriptionResponse response = subscriptionService.getMySubscription(userId);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "구독 해지", description = "현재 이용 중인 구독을 해지합니다. 다음 결제일부터 청구되지 않습니다.")
    @DeleteMapping("/{subId}")
    public ResponseEntity<Void> cancelSubscription(
            @CurrentUserId Long userId,
            @PathVariable Long subId) {
        subscriptionService.cancelSubscription(userId, subId);
        return ResponseEntity.noContent().build();
    }
}
