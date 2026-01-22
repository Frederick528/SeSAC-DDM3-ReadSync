package com.ohgiraffers.backendapi.domain.user.entity;

import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "user_informations", uniqueConstraints = { @UniqueConstraint(columnNames = {"user_name", "tag"}) })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLDelete(sql = "UPDATE users SET status = 'WITHDRAWN' WHERE user_id = ?")
@EntityListeners(AuditingEntityListener.class)
public class UserInformation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_information_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "user_name", nullable = false, length = 30)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(nullable = false)
    @Builder.Default
    private Integer experience = 0;

    @Column(name = "level_id", nullable = false)
    @Builder.Default
    private Long levelId = 1L;

    @Column(name = "preferred_genre", nullable = false)
    @Builder.Default
    private String preferredGenre = "General";

    @Column(nullable = false, length = 4)
    private String tag;


    public void addExperience(int exp) {
        this.experience += exp;
    }

    public void levelUp(Long nextLevelId) {
        this.levelId = nextLevelId;
    }

    public void update(String nickname, String profileImage, String preferredGenre) {
        if (nickname != null) this.nickname = nickname;
        if (profileImage != null) this.profileImage = profileImage;
        if (preferredGenre != null) this.preferredGenre = preferredGenre;
    }

    public void updatePreferredGenre(String preferredGenre){this.preferredGenre = preferredGenre;}

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateNicknameAndTag(String nickname, String tag) {
        this.nickname = nickname;
        this.tag = tag;
    }

}