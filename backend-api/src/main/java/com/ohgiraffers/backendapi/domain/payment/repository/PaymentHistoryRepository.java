package com.ohgiraffers.backendapi.domain.payment.repository;

import com.ohgiraffers.backendapi.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 결제 내역 엔티티를 관리하는 JPA 리포지토리입니다.
 */
@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
}
