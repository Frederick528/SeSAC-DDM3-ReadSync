package com.ohgiraffers.backendapi.global.auth.oauth.info.impl;

import com.ohgiraffers.backendapi.global.auth.oauth.info.OAuth2UserInfo;
import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> kakaoProfile;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
    }

    @Override
    public String getProviderId() {
        // 카카오 ID는 Long 타입이라 String으로 변환 필요
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getName() {
        if (kakaoProfile == null) return null;
        return (String) kakaoProfile.get("nickname");
    }

    @Override
    public String getProfileImage() {
        if (kakaoProfile == null) return null;
        return (String) kakaoProfile.get("profile_image_url");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}