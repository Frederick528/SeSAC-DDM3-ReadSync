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
import com.ohgiraffers.backendapi.global.auth.dto.TokenResponseDto;
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

    @Transactional
    public TokenResponseDto reissue(String refreshToken) {
        // 1. 리프레시 토큰 검증 (만료 여부, 위조 여부 확인)
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. 토큰에서 유저 ID(PK) 꺼내기
        // (Provider에 getUserIdFromToken 같은 메서드가 있다고 가정)
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        // 3. DB에서 저장된 리프레시 토큰 찾기
        RefreshToken storedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. 요청받은 토큰과 DB 토큰이 일치하는지 확인
        if (!storedToken.getToken().equals(refreshToken)) {
            throw new RuntimeException("토큰 정보가 일치하지 않습니다.");
        }

        // 5. 유저 역할(Role) 가져오기 (액세스 토큰 만들 때 필요)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 6. 새 액세스 토큰 발급 (리프레시 토큰은 그대로 유지하거나, 보안 강화하려면 같이 재발급)
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        // String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId()); // (선택사항: 리프레시 토큰도 갱신할 거면 주석 해제)

        // 7. 리프레시 토큰도 새로 발급했다면 DB 업데이트
        // storedToken.updateToken(newRefreshToken);

        return TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 혹은 newRefreshToken
                .build();
    }

    @Transactional
    public void logout(Long userId) {
        // DB에서 해당 유저의 리프레시 토큰 삭제
        refreshTokenRepository.deleteByUserId(userId);
    }

}