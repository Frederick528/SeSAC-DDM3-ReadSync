package com.ohgiraffers.backendapi.global.auth.oauth.service;

import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import com.ohgiraffers.backendapi.domain.user.enums.SocialProvider;
import com.ohgiraffers.backendapi.domain.user.enums.UserRole;
import com.ohgiraffers.backendapi.domain.user.enums.UserStatus;
import com.ohgiraffers.backendapi.domain.user.repository.UserInformationRepository;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.auth.oauth.info.OAuth2UserInfo;
import com.ohgiraffers.backendapi.global.auth.oauth.info.impl.GoogleOAuth2UserInfo;
import com.ohgiraffers.backendapi.global.auth.oauth.info.impl.KakaoOAuth2UserInfo;
import com.ohgiraffers.backendapi.global.auth.oauth.info.impl.NaverOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 소셜 서비스(구글, 카카오 등)에서 유저 정보를 가져온다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        // 2. 어떤 소셜 서비스인지 구분 (google, kakao, naver)
        String providerName = userRequest.getClientRegistration().getRegistrationId();

        // 3. 규격화된 DTO로 변환
        OAuth2UserInfo oAuth2UserInfo = null;
        if (providerName.equals("google")) {
            oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        } else if (providerName.equals("kakao")) {
            oAuth2UserInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
        } else if (providerName.equals("naver")) {
            oAuth2UserInfo = new NaverOAuth2UserInfo(oAuth2User.getAttributes());
        }

        // 4. 강제 로그인 로직 (회원가입 or 업데이트)
        User user = saveOrUpdate(oAuth2UserInfo);

        Map<String, Object> customAttribute = new HashMap<>(oAuth2User.getAttributes());
        customAttribute.put("loginId", user.getLoginId());
        customAttribute.put("role", user.getRole().getKey());

        // 5. 시큐리티 세션에 저장할 객체 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                customAttribute,
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
        );
    }

    private User saveOrUpdate(OAuth2UserInfo attributes) {
        // provider + providerId 로 유저를 찾는다.
        String provider = attributes.getProvider();
        String providerId = attributes.getProviderId();
        String loginId = provider + "_" + providerId;

        Optional<User> userOptional = userRepository.findByLoginId(loginId);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 기존 유저라면 정보 업데이트 (프로필 사진 등 변경되었을 수 있으므로)
            // 필요하다면 update 로직 추가 (현재는 생략됨)
        } else {
            // 신규 가입 -> DB 저장
            user = User.builder()
                    .loginId(loginId)
                    .password(java.util.UUID.randomUUID().toString()) // 비밀번호 null 에러 방지용 임시값
                    .role(UserRole.USER)
                    .status(UserStatus.ACTIVE)
                    .provider(SocialProvider.valueOf(provider.toUpperCase()))
                    .providerId(providerId)
                    .build();
            userRepository.save(user);

            String nickname = attributes.getName();
            String tag = generateUniqueTag(nickname);

            UserInformation userInfo = UserInformation.builder()
                    .user(user)
                    .nickname(nickname)
                    .tag(tag) // ★ 태그 저장 필수!
                    .profileImage(attributes.getProfileImage())
                    .preferredGenre("GENERAL")
                    .experience(0)
                    .levelId(1L)
                    .build();
            userInformationRepository.save(userInfo);
        }
        return user;
    }

    private String generateUniqueTag(String nickname) {
        String tag;
        do {
            int randomNum = new Random().nextInt(10000); // 0 ~ 9999
            tag = String.format("%04d", randomNum);      // 4자리 문자열 (예: "0012")
        } while (userInformationRepository.existsByNicknameAndTag(nickname, tag));
        return tag;
    }
}