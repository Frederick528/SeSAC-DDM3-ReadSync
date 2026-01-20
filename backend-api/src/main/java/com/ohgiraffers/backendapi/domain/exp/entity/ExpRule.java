package com.ohgiraffers.backendapi.domain.exp.entity;

import com.ohgiraffers.backendapi.domain.category.entity.Category;
import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;
import com.ohgiraffers.backendapi.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "exp_rules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ExpRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expRuleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityType activityType;

    @Column(nullable = false)
    private Integer exp;

    @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category = null; // 특정 카테고리별 보너스 경험치용 (null 가능)

    public void update(ActivityType activityType, Integer exp, Category category) {
        this.activityType = activityType;
        if (exp == null || exp < 0) {
            throw new IllegalArgumentException("경험치는 0 이상의 숫자여야 합니다.");
        }
        this.exp = exp;
        this.category = category;
    }

    public void updateExp(Integer exp) {
        if (exp == null || exp < 0) {
            throw new IllegalArgumentException("경험치는 0 이상의 숫자여야 합니다.");
        }
        this.exp = exp;
    }
}
