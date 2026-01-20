package com.ohgiraffers.backendapi.domain.exp.dto;

import com.ohgiraffers.backendapi.domain.exp.entity.ExpRule;
import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpRuleResponseDTO {
    private Long expRuleId;
    private ActivityType activityType;
    private String activityDescription;
    private Integer exp;
    private Long categoryId;

    public static ExpRuleResponseDTO from(ExpRule expRule) {
        return ExpRuleResponseDTO.builder()
                .expRuleId(expRule.getExpRuleId())
                .activityType(expRule.getActivityType())
                .activityDescription(expRule.getActivityType().getDescription())
                .exp(expRule.getExp())
                .categoryId(expRule.getCategory() != null ? expRule.getCategory().getCategoryId() : null)
                .build();
    }
}