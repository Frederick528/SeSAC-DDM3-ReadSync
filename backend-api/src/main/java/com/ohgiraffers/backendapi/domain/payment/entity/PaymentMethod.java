package com.ohgiraffers.backendapi.domain.payment.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import com.ohgiraffers.backendapi.domain.payment.enums.PgProvider;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "payment_methods")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "결제 수단 Entity")
public class PaymentMethod extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    @Schema(description = "결제 수단 ID")
    private Long methodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "사용자 정보")
    private User user;

    @Column(name = "billing_key", nullable = false)
    @Schema(description = "빌링키")
    private String billingKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "pg_provider", nullable = false, length = 20)
    @Schema(description = "PG사 제공자")
    private PgProvider pgProvider;

    @Column(name = "card_company", nullable = false, length = 20)
    @Schema(description = "카드사")
    private String cardCompany;

    @Column(name = "card_last_4", nullable = false, length = 20)
    @Schema(description = "카드 번호 뒷 4자리")
    private String cardLast4;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    @Schema(description = "기본 결제 수단 여부")
    private Boolean isDefault = false;

    // created_at, deleted_at은 BaseTimeEntity에서 상속
}
