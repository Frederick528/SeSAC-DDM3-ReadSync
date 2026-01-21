package com.ohgiraffers.backendapi.domain.contentreport.repository;

import com.ohgiraffers.backendapi.domain.contentreport.entity.ContentReport;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportProcessStatus;
import com.ohgiraffers.backendapi.domain.contentreport.enums.ContentReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentReportRepository extends JpaRepository<ContentReport, Long> {

    @Query("SELECT r FROM ContentReport r WHERE " +
            "(:status IS NULL OR r.processStatus = :status) AND " +
            "(:targetType IS NULL OR r.targetType = :targetType)")
    Page<ContentReport> findByStatusAndTargetType(
            @Param("status") ContentReportProcessStatus status,
            @Param("targetType") ContentReportTargetType targetType,
            Pageable pageable);
}
