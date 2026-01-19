package com.ohgiraffers.backendapi.domain.exp.dto;

import com.ohgiraffers.backendapi.domain.category.entity.Category;
import com.ohgiraffers.backendapi.domain.exp.entity.ExpRule;
import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExpRuleRequestDTO {
    private ActivityType activityType;
    private Integer exp;
    private Long categoryId;

    public ExpRule toEntity(Category category) {
        if (activityType == ActivityType.READ_BOOK && category == null) {
            throw new IllegalArgumentException("독서 활동(READ_BOOK)에 대한 규칙을 생성할 때는 카테고리 ID가 필수입니다.");
        }
        return ExpRule.builder()
                .activityType(this.activityType)
                .exp(this.exp)
                .category(category)
                .build();
    }
}