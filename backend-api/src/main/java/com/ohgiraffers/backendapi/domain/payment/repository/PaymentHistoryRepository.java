package com.ohgiraffers.backendapi.domain.payment.repository;

import com.ohgiraffers.backendapi.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    Optional<PaymentHistory> findByPgPaymentKey(String pgPaymentKey);
}
