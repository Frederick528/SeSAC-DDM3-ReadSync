package com.ohgiraffers.backendapi.domain.user.entity;

import com.ohgiraffers.backendapi.domain.user.enums.SocialProvider;
import com.ohgiraffers.backendapi.domain.user.enums.UserRole;
import com.ohgiraffers.backendapi.domain.user.enums.UserStatus;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserInformation userInformation;

    @Column(name = "user_email", length = 100)
    private String email;

    @Column(name = "user_name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SocialProvider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "level_id", nullable = false)
    @Builder.Default
    private Long levelId = 1L;

    // 비즈니스 로직: 레벨 업데이트
    public void updateLevel(Long newLevelId) {
        this.levelId = newLevelId;
    }

    // 비즈니스 로직: 회원 탈퇴 (Soft Delete 재정의가 필요하다면 여기에 추가 로직 작성)
    @Override
    public void delete() {
        super.delete();
        this.status = UserStatus.WITHDRAWN;
    }
}