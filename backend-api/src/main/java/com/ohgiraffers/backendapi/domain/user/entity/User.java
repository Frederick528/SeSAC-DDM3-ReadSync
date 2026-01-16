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

    // 관리자/일반 로그인용 아이디
    @Column(name = "login_id", length = 30, unique = true)
    private String loginId;

    // 관리자/일반 로그인용 비밀번호
    @Column(name = "password")
    private String password;

    @Override
    public void delete() {
        super.delete();
        this.status = UserStatus.WITHDRAWN;
    }
}