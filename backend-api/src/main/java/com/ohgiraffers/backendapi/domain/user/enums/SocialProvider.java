package com.ohgiraffers.backendapi.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialProvider {
    GOOGLE("구글"),
    KAKAO("카카오"),
    NAVER("네이버"),
    LOCAL("자체 로그인");

    private final String description;
}