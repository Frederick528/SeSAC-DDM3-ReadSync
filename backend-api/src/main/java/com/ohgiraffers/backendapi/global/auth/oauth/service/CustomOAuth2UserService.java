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

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 소셜 서비스(구글)에서 유저 정보를 가져온다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        // 2. 어떤 소셜 서비스인지 구분 (google, kakao, naver)
        String providerName = userRequest.getClientRegistration().getRegistrationId();

        // 3. 규격화된 DTO로 변환
        OAuth2UserInfo oAuth2UserInfo = null;
        if (providerName.equals("google")) {
            oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        }
        // 추후 카카오, 네이버 추가 예정
        // else if (providerName.equals("kakao")) { ... }

        // 4. 강제 로그인 로직 (회원가입 or 업데이트)
        User user = saveOrUpdate(oAuth2UserInfo);

        // 5. 시큐리티 세션에 저장할 객체 반환 (여기서 반환된 값이 SecurityContext에 저장됨)
        // 우리는 "email"을 키값으로 사용하거나, "sub" 같은 ID를 사용함.
        // attributes 맵에 "id"키를 추가해서 우리 DB의 userId를 넣어주면 나중에 편함.
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                oAuth2User.getAttributes(),
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
        );
    }

    private User saveOrUpdate(OAuth2UserInfo attributes) {
        // provider + providerId 로 유저를 찾는다. (예: google_109238...)
        // 하지만 기존 로직에 loginId가 있으니, "google_109238..."을 loginId로 쓰는 전략 사용
        String provider = attributes.getProvider();
        String providerId = attributes.getProviderId();
        String loginId = provider + "_" + providerId; // 예: google_123456789

        Optional<User> userOptional = userRepository.findByLoginId(loginId);

        User user;
        if (userOptional.isPresent()) {
            // 이미 가입된 유저 -> 정보 업데이트가 필요하면 여기서 진행 (지금은 그냥 리턴)
            user = userOptional.get();
        } else {
            // 신규 가입 -> DB 저장
            // 1. User 엔티티 생성
            user = User.builder()
                    .loginId(loginId)
                    .password(null) // 소셜은 비번 없음
                    .role(UserRole.USER) // 기본 권한
                    .status(UserStatus.ACTIVE)
                    .provider(SocialProvider.valueOf(provider.toUpperCase())) // GOOGLE
                    .providerId(providerId)
                    .build();
            userRepository.save(user);

            // 2. UserInformation 엔티티 생성 (기본 정보)
            UserInformation userInfo = UserInformation.builder()
                    .user(user)
                    .nickname(attributes.getName()) // 구글 이름 가져오기
                    .profileImage(attributes.getProfileImage()) // 구글 프사 가져오기
                    .preferredGenre("GENERAL") // 기본값
                    .experience(0)
                    .levelId(1L)
                    .build();
            userInformationRepository.save(userInfo);
        }
        return user;
    }
}