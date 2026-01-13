package com.ohgiraffers.backendapi.domain.user.dto;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import com.ohgiraffers.backendapi.domain.user.enums.SocialProvider;
import com.ohgiraffers.backendapi.domain.user.enums.UserRole;
import com.ohgiraffers.backendapi.domain.user.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @NoArgsConstructor
    public static class Join {
        private String email;
        private String name;
        private String provider;
        private String providerId;

        // User Entity 변환 메서드
        public User toUserEntity() {
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .provider(SocialProvider.valueOf(this.provider.toUpperCase()))
                    .providerId(this.providerId)
                    .role(UserRole.USER)
                    .status(UserStatus.ACTIVE)
                    .levelId(1L)
                    .build();
        }

        // UserInformation Entity 변환 메서드 (초기값 설정)
        public UserInformation toUserInformationEntity(User user) {
            return UserInformation.builder()
                    .user(user)
                    .nickname(this.name) // 초기 닉네임은 이름과 동일하게
                    .currentExp(0)
                    .totalExp(0)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String nickname;
        private String profileImg;
        private String favoriteGenre;
    }
}