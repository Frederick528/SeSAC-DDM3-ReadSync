package com.ohgiraffers.backendapi.domain.user.service;

import com.ohgiraffers.backendapi.domain.user.dto.UserRequest;
import com.ohgiraffers.backendapi.domain.user.dto.UserResponse;
import com.ohgiraffers.backendapi.domain.user.entity.RefreshToken;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import com.ohgiraffers.backendapi.domain.user.enums.SocialProvider;
import com.ohgiraffers.backendapi.domain.user.enums.UserStatus;
import com.ohgiraffers.backendapi.domain.user.repository.RefreshTokenRepository;
import com.ohgiraffers.backendapi.domain.user.repository.UserInformationRepository;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.auth.jwt.JwtTokenProvider;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ohgiraffers.backendapi.domain.user.enums.UserRole;
import com.ohgiraffers.backendapi.domain.user.enums.SocialProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse.UserLoginResponse socialLogin(UserRequest.Join request) {
        //  회원 여부 확인
        User user = userRepository.findByProviderAndProviderId(
                SocialProvider.valueOf(request.getProvider().toUpperCase()),
                request.getProviderId()
        ).orElseGet(() -> register(request));

        //  탈퇴 유저 차단
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        //  토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        //  [DB 저장] 기존 토큰 있으면 업데이트, 없으면 생성 (중복 로그인 방지)
        refreshTokenRepository.findById(user.getId())
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(user.getId())
                                        .token(refreshToken)
                                        .build()
                        )
                );

        return UserResponse.UserLoginResponse.of(accessToken, refreshToken, user, user.getUserInformation());
    }

    private User register(UserRequest.Join request) {
        User user = request.toUserEntity();
        userRepository.save(user);

        UserInformation userInfo = request.toUserInformationEntity(user);
        userInformationRepository.save(userInfo);

        return user;
    }

    @Transactional(readOnly = true)
    public UserResponse.UserDetail userInformation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserInformation userInfo = user.getUserInformation();

        return UserResponse.UserDetail.from(user, userInfo);
    }


    // 관리자 회원가입(임시)
    @Transactional
    public UserResponse.UserLoginResponse createAdmin(String loginId, String plainPassword, String nickname) {
        // 1. 중복 검사
        if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID); // ErrorCode 확인 필요
        }

        // 2. 관리자 User 생성 (비번 암호화)
        User admin = User.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode(plainPassword)) // 암호화 필수!
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .provider(SocialProvider.LOCAL)
                .providerId("ADMIN_" + loginId)
                .build();
        userRepository.save(admin);

        // 3. 관리자 정보 생성
        UserInformation adminInfo = UserInformation.builder()
                .user(admin)
                .nickname(nickname != null ? nickname : "관리자") // 닉네임 없으면 기본값
                .experience(99999) // 관리자니까 경험치 빵빵하게
                .levelId(1L)
                .preferredGenre("ALL")
                .build();
        userInformationRepository.save(adminInfo);

        // 4. ★ 토큰 발급 (로그인 로직과 동일) ★
        String accessToken = jwtTokenProvider.createAccessToken(admin.getId(), admin.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(admin.getId());

        // 5. 리프레시 토큰 저장
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(admin.getId())
                        .token(refreshToken)
                        .build()
        );

        // 6. 결과 반환
        return UserResponse.UserLoginResponse.of(accessToken, refreshToken, admin, adminInfo);
    }

    //  일반(관리자) 로그인
    @Transactional
    public UserResponse.UserLoginResponse login(UserRequest.Login request) {
        //  아이디로 찾기
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        //  탈퇴한 유저인지 확인
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // ... (토큰 발급 및 반환 로직 그대로 유지)
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        refreshTokenRepository.findById(user.getId())
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(user.getId())
                                        .token(refreshToken)
                                        .build()
                        )
                );

        return UserResponse.UserLoginResponse.of(accessToken, refreshToken, user, user.getUserInformation());
    }
}