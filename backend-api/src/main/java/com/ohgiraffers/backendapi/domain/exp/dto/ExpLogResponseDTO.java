package com.ohgiraffers.backendapi.domain.exp.dto;

import com.ohgiraffers.backendapi.domain.exp.entity.ExpLog;
import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpLogResponseDTO {
    private Long expLogId;
    private ActivityType activityType;      // READ_BOOK 등 코드값
    private String activityDescription;     // "책 읽기" 등 설명
    private Integer earnedExp;
    private Long targetId;
    private Long referenceId;
    private LocalDateTime createdAt;

    public static ExpLogResponseDTO from(ExpLog expLog) {
        return ExpLogResponseDTO.builder()
                .expLogId(expLog.getExpLogId())
                .activityType(expLog.getExpRule().getActivityType())
                .activityDescription(expLog.getExpRule().getActivityType().getDescription())
                .earnedExp(expLog.getEarnedExp())
                .targetId(expLog.getTargetId())
                .referenceId(expLog.getReferenceId())
                .createdAt(expLog.getCreatedAt())
                .build();
    }
}