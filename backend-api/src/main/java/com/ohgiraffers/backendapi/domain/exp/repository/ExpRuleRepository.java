package com.ohgiraffers.backendapi.domain.exp.repository;

import com.ohgiraffers.backendapi.domain.exp.entity.ExpRule;
import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpRuleRepository extends JpaRepository<ExpRule, Long> {

    // 특정 활동 타입에 대해 카테고리별 규칙 찾기
    Optional<ExpRule> findByActivityTypeAndCategory_CategoryId(ActivityType activityType, Long categoryId);

    // 특정 활동 타입에 대해 기본 규칙(카테고리 없는 것) 찾기
    Optional<ExpRule> findByActivityTypeAndCategoryIsNull(ActivityType activityType);
}