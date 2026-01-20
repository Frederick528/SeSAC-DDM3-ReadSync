package com.ohgiraffers.backendapi.domain.payment.repository;

import com.ohgiraffers.backendapi.domain.payment.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByBillingKey(String billingKey);

    Optional<PaymentMethod> findByUser_IdAndIsDefaultTrue(Long userId);
}
