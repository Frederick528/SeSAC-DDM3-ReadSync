package com.ohgiraffers.backendapi.domain.user.dto;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import com.ohgiraffers.backendapi.domain.user.enums.SocialProvider;
import com.ohgiraffers.backendapi.domain.user.enums.UserRole;
import com.ohgiraffers.backendapi.domain.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @NoArgsConstructor
    public static class Join {

        @NotBlank(message = "이메일은 필수항목입니다.")
        private String email;

        @NotBlank(message = "이름은 필수항목입니다.")
        private String name;

        @NotBlank(message = "소셜 제공자는 필수항목입니다.")
        private String provider; // "google", "naver" "kakao"

        @NotBlank(message = "소셜 ID는 필수항목입니다.")
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
                    .userName(this.name)
                    .experience(0)
                    .levelId(1L)
                    .preferredGenre("General")
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String userName;
        private String profileImage;
        private String preferredGenre;
    }

    // 어드민 전용 로그인 ^^
    @Getter
    @NoArgsConstructor
    public static class Login {
        @Schema(description = "로그인 아이디", example = "admin")
        @NotBlank(message = "아이디는 필수입니다.")
        private String loginId;

        @Schema(description = "비밀번호", example = "1234")
        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
    }
}