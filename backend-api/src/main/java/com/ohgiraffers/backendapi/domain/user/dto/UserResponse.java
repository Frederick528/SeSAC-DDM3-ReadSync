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
    public static class Detail {
        private Long userId;
        private String email;
        private String name;
        private String nickname;
        private String profileImg;
        private String role;
        private String status;
        private Long levelId;
        private Integer currentExp;
        private Integer totalExp;
        private String favoriteGenre;

        // Entity -> DTO 변환 메서드
        public static Detail from(User user, UserInformation userInfo) {
            return Detail.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickname(userInfo != null ? userInfo.getNickname() : user.getName())
                    .profileImg(userInfo != null ? userInfo.getProfileImg() : null)
                    .role(user.getRole().getKey())
                    .status(user.getStatus().name())
                    .levelId(user.getLevelId())
                    .currentExp(userInfo != null ? userInfo.getCurrentExp() : 0)
                    .totalExp(userInfo != null ? userInfo.getTotalExp() : 0)
                    .favoriteGenre(userInfo != null ? userInfo.getFavoriteGenre() : null)
                    .build();
        }
    }
}