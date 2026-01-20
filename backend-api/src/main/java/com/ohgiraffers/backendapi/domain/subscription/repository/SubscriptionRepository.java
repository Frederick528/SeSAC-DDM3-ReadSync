package com.ohgiraffers.backendapi.domain.subscription.repository;

import com.ohgiraffers.backendapi.domain.subscription.entity.Subscription;
import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUser_IdAndStatus(Long userId, SubscriptionStatus status);
}
