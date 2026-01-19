package com.ohgiraffers.backendapi.domain.exp.entity;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "exp_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ExpLog extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exp_rule_id", nullable = false)
    private ExpRule expRule;

    @Column(nullable = false)
    private Integer earnedExp;

    @Column(nullable = false)
    private Long targetId;    // 경험치 획득 활동 관련 ID (예: 리뷰 ID, 출석 날짜)

    @Column(nullable = false)
    private Long referenceId; // 유니크 체크용 참조 ID (예: 리뷰-도서ID, 출석 날짜-출석 날짜)
}
