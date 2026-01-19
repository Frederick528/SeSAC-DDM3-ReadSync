package com.ohgiraffers.backendapi.domain.exp.repository;

import com.ohgiraffers.backendapi.domain.exp.entity.ExpLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExpLogRepository extends JpaRepository<ExpLog, Long> {

    @EntityGraph(attributePaths = {"expRule"}) // ExpRule을 한 번에 fetch join
    List<ExpLog> findAllByUser_Id(Long userId);
    // 중복 보상 여부 확인 (유니크 키 조건과 동일)
    boolean existsByUser_IdAndExpRule_ExpRuleIdAndReferenceId(Long userId, Long expRuleId, Long referenceId);
}
