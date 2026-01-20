package com.ohgiraffers.backendapi.domain.subscription.entity;

import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionPlan;
import com.ohgiraffers.backendapi.domain.subscription.enums.SubscriptionStatus;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "구독 Entity")
public class Subscription extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_id")
    @Schema(description = "구독 ID")
    private Long subId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "사용자")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_name", nullable = false, length = 50)
    @Schema(description = "플랜 명")
    private SubscriptionPlan planName;

    @Column(name = "price", nullable = false)
    @Schema(description = "구독 가격")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Schema(description = "구독 상태")
    private SubscriptionStatus status;

    @Column(name = "next_billing_date", nullable = false)
    @Schema(description = "다음 결제일")
    private LocalDateTime nextBillingDate;

    @Column(name = "started_at")
    @Schema(description = "최초 시작일")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    @Schema(description = "구독 종료일")
    private LocalDateTime endedAt;

    public void cancel() {
        this.status = SubscriptionStatus.CANCELED;
        // nextBillingDate maintains its value, subscription is valid until then
    }

    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
    }
}
