package com.ohgiraffers.backendapi.domain.subscription.repository;

import com.ohgiraffers.backendapi.domain.subscription.entity.Subscription;
import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionStatus;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 구독 엔티티를 관리하는 JPA 리포지토리입니다.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * 특정 유저의 활성화된 구독 정보를 조회합니다.
     */
    Optional<Subscription> findByUserAndSubscriptionStatus(User user, SubscriptionStatus status);

    /**
     * 결제 예정일이 지났거나 도달한 활성 구독 목록을 조회합니다 (배치 처리용).
     */
    List<Subscription> findBySubscriptionStatusAndNextBillingDateBefore(SubscriptionStatus status,
            LocalDateTime dateTime);
}
