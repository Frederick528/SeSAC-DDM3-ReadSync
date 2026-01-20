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

        @NotBlank(message = "소셜 제공자는 필수항목입니다.")
        private String provider; // "google", "naver", "kakao"

        @NotBlank(message = "소셜 ID는 필수항목입니다.")
        private String providerId;

        private String nickname;
        private String profileImage;

        public User toUserEntity() {
            return User.builder()
                    .provider(SocialProvider.valueOf(this.provider.toUpperCase()))
                    .providerId(this.providerId)
                    .role(UserRole.USER)
                    .status(UserStatus.ACTIVE)
                    .build();
        }

        public UserInformation toUserInformationEntity(User user, String tag) {
            return UserInformation.builder()
                    .user(user)
                    .nickname(this.nickname)
                    .tag(tag)
                    .profileImage(this.profileImage)
                    .experience(0)
                    .levelId(1L)
                    .preferredGenre("General")
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "유저 정보 수정 요청")
    public static class UpdateProfile {

        @Schema(description = "변경할 닉네임", example = "책읽는선비")
        private String nickname;
        @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.png")
        private String profileImage;
        @Schema(description = "선호 장르", example = "소설")
        private String preferredGenre;
    }

    @Getter
    @NoArgsConstructor
    public static class AdminSignup {
        @Schema(description = "로그인 아이디", example = "admin")
        @NotBlank(message = "아이디는 필수입니다.")
        private String loginId;

        @Schema(description = "비밀번호", example = "1234")
        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;

        @Schema(description = "관리자 닉네임", example = "총관리자")
        @NotBlank(message = "닉네임은 필수입니다.")
        private String nickname;
    }

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