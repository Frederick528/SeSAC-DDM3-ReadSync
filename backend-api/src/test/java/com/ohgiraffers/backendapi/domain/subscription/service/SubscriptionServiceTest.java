package com.ohgiraffers.backendapi.domain.subscription.service;

import com.ohgiraffers.backendapi.domain.credit.service.CreditService;
import com.ohgiraffers.backendapi.domain.subscription.entity.Subscription;
import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionPlan;
import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionStatus;
import com.ohgiraffers.backendapi.domain.subscription.repository.SubscriptionRepository;
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
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CreditService creditService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    @DisplayName("구독 생성 성공 - 결제 대기 상태로 생성되어야 함")
    void createSubscription_Success() {
        // given
        Long userId = 1L;
        SubscriptionPlan plan = SubscriptionPlan.BASIC;
        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUser_IdAndStatus(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Subscription result = subscriptionService.createSubscription(userId, plan);

        // then
        assertNotNull(result);
        assertEquals(SubscriptionStatus.PENDING_PAYMENT, result.getStatus());
        assertEquals(plan.getPrice().longValue(), result.getPrice().longValue());

        // 크레딧은 바로 지급되지 않아야 함
        verify(creditService, never()).addCredits(any(), anyInt(), anyString(), anyInt());
    }

    @Test
    @DisplayName("구독 활성화 - 상태가 ACTIVE로 변경되고 크레딧이 지급되어야 함")
    void activateSubscription_Success() {
        // given
        Long subId = 100L;
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        Subscription subscription = Subscription.builder()
                .subId(subId)
                .user(user)
                .planName(SubscriptionPlan.BASIC)
                .status(SubscriptionStatus.PENDING_PAYMENT)
                .build();

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(subscription));

        // when
        subscriptionService.activateSubscription(subId);

        // then
        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        verify(creditService, times(1)).addCredits(eq(user), eq(100), eq("PREMIUM"), eq(30));
    }

    @Test
    @DisplayName("구독 해지 성공")
    void cancelSubscription_Success() {
        // given
        Long userId = 1L;
        Subscription subscription = Subscription.builder()
                .user(User.builder().id(userId).build())
                .status(SubscriptionStatus.ACTIVE)
                .build();

        when(subscriptionRepository.findByUser_IdAndStatus(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));

        // when
        subscriptionService.cancelSubscription(userId);

        // then
        assertEquals(SubscriptionStatus.CANCELED, subscription.getStatus());
    }
}
