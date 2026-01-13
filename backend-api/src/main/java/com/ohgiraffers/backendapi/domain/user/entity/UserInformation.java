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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "user_name", nullable = false, length = 30)
    private String userName;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(nullable = false)
    @Builder.Default
    private Integer experience = 0; // 단일 변수로 수정

    // SQL 명세에 따라 level_id는 여기에 위치함
    @Column(name = "level_id", nullable = false)
    @Builder.Default
    private Long levelId = 1L;

    @Column(name = "preferred_genre", nullable = false) // NOT NULL 확인 필요 (일단 기본값이나 필수 입력 처리)
    @Builder.Default
    private String preferredGenre = "General"; // 가입 시 기본값 설정

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 비즈니스 로직: 경험치 추가
    public void addExperience(int exp) {
        this.experience += exp;
    }

    // 비즈니스 로직: 레벨 업
    public void levelUp(Long nextLevelId) {
        this.levelId = nextLevelId;
    }
}