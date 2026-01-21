package com.ohgiraffers.backendapi.global.auth.oauth.info;

import java.util.Map;

public interface OAuth2UserInfo {
    String getProviderId();   // 소셜 식별자 (구글은 sub, 카카오는 id, 네이버는 id)
    String getProvider();     // 소셜 타입 (google, kakao, naver)
    String getEmail();        // 이메일
    String getName();         // 이름 (닉네임)
    String getProfileImage(); // 프로필 사진 URL
    Map<String, Object> getAttributes(); // 소셜에서 받은 원본 데이터
}