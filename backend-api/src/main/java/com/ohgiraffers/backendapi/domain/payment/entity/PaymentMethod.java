package com.ohgiraffers.backendapi.domain.payment.entity;

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
public class PaymentMethod extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private Long methodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "billing_key", nullable = false)
    private String billingKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "pg_provider", nullable = false, length = 20)
    private PgProvider pgProvider;

    @Column(name = "card_company", nullable = false, length = 20)
    private String cardCompany;

    @Column(name = "card_last_4", nullable = false, length = 20)
    private String cardLast4;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "customer_key", nullable = true)
    private String customerKey;

    // created_at, deleted_at은 BaseTimeEntity에서 상속
    public void updateDefaultStatus(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
