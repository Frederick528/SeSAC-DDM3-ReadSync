package com.ohgiraffers.backendapi.domain.payment.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import com.ohgiraffers.backendapi.domain.order.entity.Order;
import com.ohgiraffers.backendapi.domain.payment.enums.PaymentStatus;
import com.ohgiraffers.backendapi.domain.payment.enums.PgProvider;
import com.ohgiraffers.backendapi.domain.payment.enums.TransactionType;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_history")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "결제 내역 Entity")
public class PaymentHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    @Schema(description = "결제 내역 ID")
    private Long historyId;

    @Column(name = "pg_payment_key", nullable = false)
    @Schema(description = "PG사 결제 키")
    private String pgPaymentKey;

    @Column(name = "amount", nullable = false)
    @Schema(description = "결제 금액")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Schema(description = "결제 상태")
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "trans_type", nullable = false, length = 10)
    @Schema(description = "거래 유형")
    private TransactionType transType;

    @Enumerated(EnumType.STRING)
    @Column(name = "pg_provider", nullable = false, length = 20)
    @Schema(description = "PG사 제공자")
    private PgProvider pgProvider;

    @Column(name = "cancel_reason")
    @Schema(description = "취소 사유")
    private String cancelReason;

    // created_at은 BaseTimeEntity에서 상속

    @Column(name = "receipt_url", length = 500)
    @Schema(description = "영수증 URL")
    private String receiptUrl;

    @Column(name = "fail_reason")
    @Schema(description = "실패 사유")
    private String failReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @Schema(description = "주문 정보")
    private Order order;
}
