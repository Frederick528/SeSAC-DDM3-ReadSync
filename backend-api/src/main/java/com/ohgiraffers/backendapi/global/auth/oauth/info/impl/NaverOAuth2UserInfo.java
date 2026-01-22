package com.ohgiraffers.backendapi.global.auth.oauth.info.impl;

import com.ohgiraffers.backendapi.global.auth.oauth.info.OAuth2UserInfo;
import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes; // 전체 데이터
    private final Map<String, Object> response;   // "response" 내부 데이터

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        if (response == null) return null;
        return (String) response.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        if (response == null) return null;
        return (String) response.get("email");
    }

    @Override
    public String getName() {
        if (response == null) return null;
        return (String) response.get("name");
    }

    @Override
    public String getProfileImage() {
        if (response == null) return null;
        return (String) response.get("profile_image");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}