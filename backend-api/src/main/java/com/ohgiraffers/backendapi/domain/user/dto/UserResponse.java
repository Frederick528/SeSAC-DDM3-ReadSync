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
    public static class Login {
        private String accessToken;
        private String refreshToken;
        private Detail userDetail;

        public static Login of(String accessToken, String refreshToken, User user, UserInformation userInfo) {
            return Login.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userDetail(Detail.from(user, userInfo))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Detail {
        private Long userId;
        private String email;
        private String name;
        private String userName;
        private String profileImage;
        private String role;
        private String status;
        private Long levelId;
        private Integer experience;
        private String preferredGenre;

        public static Detail from(User user, UserInformation userInfo) {
            return Detail.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .userName(userInfo != null ? userInfo.getUserName() : user.getName())
                    .profileImage(userInfo != null ? userInfo.getProfileImage() : null)
                    .role(user.getRole().getKey())
                    .status(user.getStatus().name())
                    .levelId(userInfo != null ? userInfo.getLevelId() : 1L)
                    .experience(userInfo != null ? userInfo.getExperience() : 0)
                    .preferredGenre(userInfo != null ? userInfo.getPreferredGenre() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Profile {
        private Long userId;
        private String email;
        private String name;
        private String profileImage;
        private int experience;
        private String preferredGenre;

        // 엔티티 -> DTO 변환 메서드
        public static Profile from(User user) {
            return Profile.builder()
                    .userId(user.getId())
                    .email(user.getLoginId()) // 소셜이면 email, 일반이면 loginId
                    .name(user.getUserInformation().getUserName())
                    .profileImage(user.getUserInformation().getProfileImage())
                    .experience(user.getUserInformation().getExperience())
                    .preferredGenre(user.getUserInformation().getPreferredGenre())
                    .build();
        }
    }
}