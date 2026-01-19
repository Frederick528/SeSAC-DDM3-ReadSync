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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserResponse.Login socialLogin(UserRequest.Join request) {
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

        return UserResponse.Login.of(accessToken, refreshToken, user, user.getUserInformation());
    }

    private User register(UserRequest.Join request) {
        User user = request.toUserEntity();
        userRepository.save(user);

        UserInformation userInfo = request.toUserInformationEntity(user);
        userInformationRepository.save(userInfo);

        return user;
    }

    @Transactional(readOnly = true)
    public UserResponse.Detail userInformation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserInformation userInfo = user.getUserInformation();

        return UserResponse.Detail.from(user, userInfo);
    }

    @Transactional
    public UserResponse.Detail updateInformation(Long userId, UserRequest.UserUpdate request) {
        // 1. 유저 정보 엔티티 조회
        // (UserInformationRepository에 findByUserId(Long userId) 메서드가 선언되어 있어야 합니다)
        UserInformation userInfo = userInformationRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 엔티티 데이터 업데이트
        userInfo.update(
                request.getNickname(),
                request.getProfileImage(),
                request.getPreferredGenre()
        );

        // 3. 변경된 엔티티를 DTO로 변환하여 반환
        return UserResponse.Detail.from(userInfo.getUser(), userInfo);
    }

    //  일반(관리자) 로그인
    @Transactional
    public UserResponse.Login login(UserRequest.Login request) {
        //  아이디로 찾기
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        //  비밀번호 확인 (단순 문자열 비교)
        //  나중에 Security 설정하면 passwordEncoder.matches()로 바꿔야 함
        if (!request.getPassword().equals(user
                .getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        //  탈퇴한 유저인지 확인
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        //  토큰 발급 (소셜 로그인과 동일)
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        //  리프레시 토큰 저장
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

        return UserResponse.Login.of(accessToken, refreshToken, user, user.getUserInformation());
    }
}