package com.ohgiraffers.backendapi.domain.user.dto;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UserLoginResponse {
        private String accessToken;
        private String refreshToken;
        private UserDetail detail;

        public static UserLoginResponse of(String accessToken, String refreshToken, User user, UserInformation userInfo) {
            return UserLoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .detail(UserDetail.from(user, userInfo))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UserDetail {
        private Long userId;
        private String nickname;
        private String profileImage;
        private String role;
        private String status;
        private Long levelId;
        private int experience;
        private String preferredGenre;

        public static UserDetail from(User user, UserInformation userInfo) {
            return UserDetail.builder()
                    .userId(user.getId())
                    .profileImage(userInfo != null ? userInfo.getProfileImage() : null)
                    .role(user.getRole().getKey())
                    .status(user.getStatus().name())
                    .levelId(userInfo != null ? userInfo.getLevelId() : 1L)
                    .experience(userInfo != null ? userInfo.getExperience() : 0)
                    .preferredGenre(userInfo != null ? userInfo.getPreferredGenre() : "General")
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Profile {
        private Long userId;
        private String nickname;
        private String profileImage;
        private int experience;
        private String preferredGenre;
        private String providerId;

        // 엔티티 -> DTO 변환 메서드
        public static Profile from(User user) {
            return Profile.builder()
                    .userId(user.getId())
                    .providerId(user.getProviderId())
                    .nickname(user.getUserInformation().getNickname())
                    .profileImage(user.getUserInformation().getProfileImage())
                    .experience(user.getUserInformation().getExperience())
                    .preferredGenre(user.getUserInformation().getPreferredGenre())
                    .build();
        }
    }
}