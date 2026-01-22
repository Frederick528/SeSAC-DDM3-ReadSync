package com.ohgiraffers.backendapi.domain.user.service;

import com.ohgiraffers.backendapi.domain.user.dto.UserRequest;
import com.ohgiraffers.backendapi.domain.user.dto.UserResponse;
import com.ohgiraffers.backendapi.domain.user.entity.RefreshToken;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.domain.user.entity.UserInformation;
import com.ohgiraffers.backendapi.domain.user.enums.SocialProvider;
import com.ohgiraffers.backendapi.domain.user.enums.UserRole;
import com.ohgiraffers.backendapi.domain.user.enums.UserStatus;
import com.ohgiraffers.backendapi.domain.user.repository.RefreshTokenRepository;
import com.ohgiraffers.backendapi.domain.user.repository.UserInformationRepository;
import com.ohgiraffers.backendapi.domain.user.repository.UserRepository;
import com.ohgiraffers.backendapi.global.auth.dto.TokenResponseDto;
import com.ohgiraffers.backendapi.global.auth.jwt.JwtTokenProvider;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // Redis 템플릿 주입 (RedisConfig에서 설정한 이름과 타입 일치)
    private final RedisTemplate<String, Object> redisTemplate;

    // 1. 소셜 로그인
    @Transactional
    public UserResponse.UserLoginResponse socialLogin(UserRequest.Join request) {
        User user = userRepository.findByProviderAndProviderId(
                SocialProvider.valueOf(request.getProvider().toUpperCase()),
                request.getProviderId()
        ).orElseGet(() -> register(request));

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        return issueTokens(user); // 내부에서 킥 신호 발송함
    }

    // (내부 메서드) 회원가입 처리
    private User register(UserRequest.Join request) {
        User user = request.toUserEntity();
        userRepository.save(user);

        String tag = generateUniqueTag(request.getNickname());

        UserInformation userInfo = UserInformation.builder()
                .user(user)
                .nickname(request.getNickname())
                .tag(tag)
                .profileImage(request.getProfileImage())
                .experience(0)
                .levelId(1L)
                .preferredGenre("General")
                .build();

        userInformationRepository.save(userInfo);

        return user;
    }

    // 2. 유저 상세 정보 조회
    @Transactional(readOnly = true)
    public UserResponse.UserDetail userInformation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserInformation userInfo = user.getUserInformation();

        return UserResponse.UserDetail.from(user, userInfo);
    }

    // 3. 관리자 회원가입
    @Transactional
    public UserResponse.UserLoginResponse createAdmin(String loginId, String plainPassword, String nickname) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        User admin = User.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode(plainPassword))
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .provider(SocialProvider.LOCAL)
                .providerId("ADMIN_" + loginId)
                .build();
        userRepository.save(admin);

        String finalNickname = (nickname != null) ? nickname : "관리자";
        String tag = generateUniqueTag(finalNickname);

        UserInformation adminInfo = UserInformation.builder()
                .user(admin)
                .nickname(finalNickname)
                .tag(tag)
                .experience(99999)
                .levelId(1L)
                .preferredGenre("ALL")
                .build();
        userInformationRepository.save(adminInfo);

        return issueTokens(admin);
    }

    // 4. 로그인
    @Transactional
    public UserResponse.UserLoginResponse login(UserRequest.Login request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        return issueTokens(user);
    }

    // 5. 토큰 재발급
    @Transactional
    public TokenResponseDto reissue(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (!storedToken.getToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());

        return TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 6. 로그아웃
    @Transactional
    public void logout(Long userId) {
        // DB에서 리프레시 토큰 삭제
        refreshTokenRepository.deleteByUserId(userId);

        //  로그아웃 시에도 소켓 끊으라고 신호 보냄
        publishKickEvent(userId);
    }


    //  토큰 발급 공통 로직 + 킥 이벤트 발행
    private UserResponse.UserLoginResponse issueTokens(User user) {

        publishKickEvent(user.getId());

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

    private void publishKickEvent(Long userId) {
        try {
            redisTemplate.convertAndSend("user-kick", String.valueOf(userId));
            log.info("Kick event published for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to publish kick event for user: {}", userId, e);
        }
    }

    // 태그 생성기
    private String generateUniqueTag(String nickname) {
        String tag;
        do {
            int randomNum = new Random().nextInt(10000);
            tag = String.format("%04d", randomNum);
        } while (userInformationRepository.existsByNicknameAndTag(nickname, tag));
        return tag;
    }
}