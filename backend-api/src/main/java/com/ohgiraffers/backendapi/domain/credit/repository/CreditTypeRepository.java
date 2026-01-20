package com.ohgiraffers.backendapi.domain.credit.repository;

import com.ohgiraffers.backendapi.domain.credit.entity.CreditType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditTypeRepository extends JpaRepository<CreditType, Long> {
    Optional<CreditType> findByUser_IdAndCreditName(Long userId, String creditName);
}
