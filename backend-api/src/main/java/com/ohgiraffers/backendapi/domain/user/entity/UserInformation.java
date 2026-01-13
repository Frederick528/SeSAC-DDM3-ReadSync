package com.ohgiraffers.backendapi.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_informations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_information_id")
    private Long id;

    // User와 1:1 관계 (주인은 UserInformation으로 설정하여 FK 관리)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "current_exp", nullable = false)
    @Builder.Default
    private Integer currentExp = 0;

    @Column(name = "total_exp", nullable = false)
    @Builder.Default
    private Integer totalExp = 0;

    @Column(name = "favorite_genre", length = 50)
    private String favoriteGenre;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 비즈니스 로직: 경험치 업데이트
    public void addExperience(int exp) {
        this.currentExp += exp;
        this.totalExp += exp;
    }
}