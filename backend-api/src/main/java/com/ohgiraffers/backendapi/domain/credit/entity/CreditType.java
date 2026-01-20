package com.ohgiraffers.backendapi.domain.credit.entity;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "credit_type")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "크레딧 타입 Entity")
public class CreditType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credit_type_id")
    @Schema(description = "크레딧 타입 ID")
    private Long creditTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "사용자")
    private User user;

    @Column(name = "credit_name", nullable = false, length = 20)
    @Schema(description = "크레딧 이름 (FREE/PREMIUM)")
    private String creditName;

    @Column(name = "base_expiry_days")
    @Schema(description = "기본 유효 기간(일)")
    private Integer baseExpiryDays;
}
