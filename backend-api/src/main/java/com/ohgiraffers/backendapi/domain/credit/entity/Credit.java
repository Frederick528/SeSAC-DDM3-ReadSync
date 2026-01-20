package com.ohgiraffers.backendapi.domain.credit.entity;

import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "credits")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "크레딧 Entity")
public class Credit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credits_id")
    @Schema(description = "크레딧 ID")
    private Long creditsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_type", nullable = false)
    @Schema(description = "크레딧 타입")
    private CreditType creditType;

    @Column(name = "credits", nullable = false)
    @Schema(description = "크레딧 수량")
    private Integer credits;

    @Column(name = "status", nullable = false, length = 20)
    @Schema(description = "상태 (ACTIVE, EXPIRED, USED_UP)")
    private String status;

    // BaseTimeEntity handles created_at

    @Column(name = "deleted_at")
    @Schema(description = "삭제일")
    private LocalDateTime deletedAt;

    public void deduct(int amount) {
        this.credits -= amount;
    }

    public void useUp() {
        this.credits = 0;
        this.status = "USED_UP";
    }
}
