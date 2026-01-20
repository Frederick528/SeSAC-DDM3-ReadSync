package com.ohgiraffers.backendapi.domain.subscription.service;

import com.ohgiraffers.backendapi.domain.credit.service.CreditService;
import com.ohgiraffers.backendapi.domain.subscription.entity.Subscription;
import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionPlan;
import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionStatus;
import com.ohgiraffers.backendapi.domain.subscription.repository.SubscriptionRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CreditService creditService;
    private final UserRepository userRepository;

    @Transactional
    public Subscription createSubscription(Long userId, SubscriptionPlan plan) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Check if active subscription exists
        subscriptionRepository.findByUser_IdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .ifPresent(s -> {
                    throw new CustomException(ErrorCode.ALREADY_SUBSCRIBED);
                });

        Subscription subscription = Subscription.builder()
                .user(user)
                .planName(plan)
                .price(BigDecimal.valueOf(plan.getPrice()))
                .status(SubscriptionStatus.PENDING_PAYMENT)
                .startedAt(LocalDateTime.now())
                .nextBillingDate(LocalDateTime.now().plusMonths(1))
                .build();

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void activateSubscription(Long subId) {
        Subscription subscription = subscriptionRepository.findById(subId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        // Update status logic here (need setter or method in Entity)
        subscription.activate();

        // Add credits
        creditService.addCredits(subscription.getUser(), subscription.getPlanName().getCredits(), "PREMIUM", 30);
    }

    @Transactional
    public void cancelSubscription(Long userId) {
        Subscription subscription = subscriptionRepository.findByUser_IdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        subscription.cancel();
    }
}
