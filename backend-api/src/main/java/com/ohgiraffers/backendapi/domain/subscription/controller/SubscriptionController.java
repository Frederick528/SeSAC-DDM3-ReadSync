package com.ohgiraffers.backendapi.domain.subscription.controller;

import com.ohgiraffers.backendapi.domain.subscription.entity.Subscription;
import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionPlan;
import com.ohgiraffers.backendapi.domain.subscription.service.SubscriptionService;
import com.ohgiraffers.backendapi.global.common.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscription", description = "구독 관리 API")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @Operation(summary = "구독 신청", description = "사용자의 구독을 생성하고 크레딧을 지급합니다.")
    public ResponseEntity<String> subscribe(@CurrentUserId Long userId, @RequestParam SubscriptionPlan plan) {
        subscriptionService.createSubscription(userId, plan);
        return ResponseEntity.ok("Subscription created successfully");
    }

    @PostMapping("/cancel")
    @Operation(summary = "구독 해지", description = "사용자의 활성 구독을 해지 예약 상태로 변경합니다.")
    public ResponseEntity<String> cancelSubscription(@CurrentUserId Long userId) {
        subscriptionService.cancelSubscription(userId);
        return ResponseEntity.ok("Subscription canceled successfully");
    }
}
