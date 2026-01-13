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

        public User toUserEntity() {
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .provider(SocialProvider.valueOf(this.provider.toUpperCase()))
                    .providerId(this.providerId)
                    .role(UserRole.USER)
                    .status(UserStatus.ACTIVE)
                    .build();
        }

        public UserInformation toUserInformationEntity(User user) {
            return UserInformation.builder()
                    .user(user)
                    .userName(this.name) // 초기 user_name은 가입 시 이름으로
                    .experience(0)
                    .levelId(1L)
                    .preferredGenre("General") // 초기값
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String userName; // nickname -> userName
        private String profileImage;
        private String preferredGenre;
    }
}