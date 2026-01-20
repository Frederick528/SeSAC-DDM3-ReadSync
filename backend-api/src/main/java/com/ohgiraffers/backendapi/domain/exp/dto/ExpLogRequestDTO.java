package com.ohgiraffers.backendapi.domain.exp.dto;

import com.ohgiraffers.backendapi.domain.exp.entity.ExpLog;
import com.ohgiraffers.backendapi.domain.exp.entity.ExpRule;
import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpLogRequestDTO {
    private Long userId;
    private ActivityType activityType; // 룰 ID 대신 타입을 받음
    private Long categoryId;           // 타입을 통해 룰을 찾기 위한 재료
    private Long targetId;
    private Long referenceId;

    // toEntity는 서비스에서 찾은 ExpRule을 주입받아 처리
    public ExpLog toEntity(User user, ExpRule expRule) {
        return ExpLog.builder()
                .user(user)
                .expRule(expRule)
                .earnedExp(expRule.getExp())
                .targetId(this.targetId)
                .referenceId(this.referenceId)
                .build();
    }
}