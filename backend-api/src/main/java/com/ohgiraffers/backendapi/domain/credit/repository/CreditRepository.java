package com.ohgiraffers.backendapi.domain.credit.repository;

import com.ohgiraffers.backendapi.domain.credit.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    List<Credit> findByCreditType_CreditTypeIdAndStatus(Long creditTypeId, String status);
}
